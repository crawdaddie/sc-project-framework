SaveHooks {
  classvar <>hooks;
  *initClass {
    hooks = Dictionary();
  }
  *addHook { arg path, hook;
    hooks[path] = (hooks[path] ?? []) ++ [hook];
  }
  *savePath { arg path;
    hooks[path].do { arg hook;
      hook.value();
    }
  }
  *getDataPath { arg path;
    ^path.dirname +/+ "." ++ path.basename.replace(".scd", ".data.scd");
  }
}
