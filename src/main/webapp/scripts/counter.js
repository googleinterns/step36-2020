let loadingCounter = (function(value = 0) {

  const getValue = function() {
    return value;
  }

  const changeBy = function (n) {
    if (isNaN(n)) {
      throw `${n} is not a number`;
    }
    value += n;
    hide();
    return value;
  }

  const hide = function() {
    if (value < 0) {
      $('body').removeClass('loading');
    }
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
