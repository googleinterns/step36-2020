package com.google.sps.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import com.google.gson.Gson;
import com.google.sps.data.Keywords;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-image")
public class ImageServlet extends HttpServlet {

  /**
   * Writes an upload URL for file uploads to the servlet.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstore.createUploadUrl("/user-image");
    response.getWriter().println(uploadUrl);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter writer = response.getWriter();
    BlobKey blobKey = getBlobKey(request, "image");
    if (blobKey == null) { // TODO: Check whether file is an image.
      response.sendRedirect("/index.html");
      return;
    }
    byte[] blobBytes = getBlobBytes(blobKey);
    String key = "";
    TextAnnotation textAnnotation = getImageText(blobBytes);
    if (textAnnotation == null || textAnnotation.getText().equals("")) {
      key = Keywords.addKeywords(getImageLabels(blobBytes));
    } else {
      key = Keywords.addKeywords(textAnnotation.getText());
    }
    response.sendRedirect(String.format("/results?k=%s", key));
  }

  /**
   * Returns the BlobKey that points to the file uploaded by the user, or null if the user didn't
   * upload a file.
   */
  private BlobKey getBlobKey(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so the BlobKey is empty (live).
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    return blobKey;
  }

  /**
   * Retrieves the binary data stored at the BlobKey parameter.
   */
  private byte[] getBlobBytes(BlobKey blobKey) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
    int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
    long currentByteIndex = 0;
    boolean continueReading = true;
    while (continueReading) {
      // End index is inclusive, so we have to subtract 1 to get fetchSize bytes.
      byte[] b =
          blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
      outputBytes.write(b);

      // If we read fewer bytes than we requested, then we reached the end.
      continueReading = b.length >= fetchSize;
      currentByteIndex += fetchSize;
    }
    return outputBytes.toByteArray();
  }

  /**
   * Creates a list of labels that apply to the image, which is
   * represented by the binary data stored in imgBytes.
   */
  private List<EntityAnnotation> getImageLabels(byte[] imgBytes) throws IOException {
    AnnotateImageResponse imageResponse = getImageResponse(imgBytes, false);    
    return imageResponse.getLabelAnnotationsList();
  }

  /**
   * Gets the text within the image, whic is represented by
   * the binary data stored in imgBytes.
   */
  private TextAnnotation getImageText(byte[] imgBytes) throws IOException {
    AnnotateImageResponse imageResponse = getImageResponse(imgBytes, true);
    return imageResponse.getFullTextAnnotation();
  }

  /**
   * Gets an AnnotateImageResponse corresponding to the image represented by an array of bytes, and the type
   * of detection to use. If useTextDetection is false, then the default is to use label detection.
   */
  private AnnotateImageResponse getImageResponse(byte[] imgBytes, boolean useTextDetection) throws IOException {
    ByteString byteString = ByteString.copyFrom(imgBytes);
    Image image = Image.newBuilder().setContent(byteString).build();
    Feature.Type detectionMethod = (useTextDetection) ? Feature.Type.TEXT_DETECTION : Feature.Type.LABEL_DETECTION;
    Feature feature = Feature.newBuilder().setType(detectionMethod).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    ImageAnnotatorClient client = ImageAnnotatorClient.create();
    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
    client.close();
    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
    AnnotateImageResponse imageResponse = imageResponses.get(0);

    if (imageResponse.hasError()) {
      String errorType = (useTextDetection) ? "text" : "labels";
      System.err.println(String.format("Error getting image %s: %s ", errorType, imageResponse.getError().getMessage()));
      return null;
    }
    return imageResponse;
  }
}

