let counter = (function(value = 0) {

  const getValue = function() {
    return value;
  }

  const changeBy = function (n) {
    if (isNaN(n)) {
      throw `${n} is not a number`;
    }
    value += n;
    return value;
  }


  const add = function(n) {
    return changeBy(n);
  }

  const subtract = function(n) {
    return changeBy(-n);
  }

  return {
    getValue : getValue,
    add : add,
    increment : () => { return add(1); },
    subtract : subtract,
    decrement : () => { return subtract(1); },
  }
})();

function traceCounterMethods(counter, total) {
  const handlers = {
    get: function(target, propKey, receiver) {
      const method = target[propKey];
      return function (...args) {
        let result = method.apply(this, args);
        if (result < 0) {
          $('body').removeClass('loading');
        }
        let progress = (1 - (result + 1) / (total + 1)) * 100;
        let progressString = `${Math.floor(progress)}%`;
        console.log(progressString);
        $("#loading-bar::after").animate({width : progressString});
        return result;
      };
    }
  };
  return new Proxy(counter, handlers);
}
