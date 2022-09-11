+ Mod {
  keyDownHandler {
    ^{ arg v, char, modifier, unicode, keycode, key;
      if ([modifier, unicode] == [262144, 19], { // ctrl-s
        this.save;
      });
    }
  }
}
