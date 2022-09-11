D {
  *new { arg key, object;
    var savedUserData = currentEnvironment.userData;
    var data = savedUserData.data[key] ?? ();
    data.postln;
    currentEnvironment.put(key, object.putAll(data));

    currentEnvironment.addPreSaveHook({ arg mod;
      mod.userData.data[key] = mod[key]; 
    });
    object.apply;

    ^object;
  }
}

