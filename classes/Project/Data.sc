Data : Environment {
  var <>savePath;
  var <>defaults;
  var <>presets;
  var >fromData, >toData;
  *new { arg defaults = (), presets = (), fromData = {}, toData = {};
    ^super.new.init(defaults, presets, fromData, toData);
  }

  loadPresets { arg data;
    var localPresets = if (File.exists(savePath), { savePath.load.presets }, { () });
    ^localPresets.putAll(data.presets ?? ());
  }

  loadData {
    var data = if (Project.current.notNil,
      {
        var projectData = Project.current[savePath.asSymbol] ?? ();
        projectData.postln;
        projectData;
      }, {
        ^if (File.exists(savePath), {
          savePath.load
        }, {
          "archive % not found".format(savePath).postln;
          ()
        })
      }
    );
    ^data;
  }

  init { arg argDefaults, argPresets, argFromData, argToData;
    var path = thisProcess.nowExecutingPath;
    var data;
    savePath = path.dirname +/+ "." ++ path.basename.replace(".scd", ".data.scd");
    defaults = argDefaults;
    data = this.loadData;
    presets = argPresets.putAll(this.loadPresets(data));
    this.putAll(defaults);
    this.putAll(data);
    this.know_(true);
    fromData = argFromData;
    toData = argToData;
    currentEnvironment.use {
      ~save = M {
        var data = this.toData;
        this.postln;
        this.putAll(data);
        this.save;
      };
      ~savePreset = M { arg presetName;
        presetName.postln;
      };
    };
    this.save;
  }

  savePreset { arg name, input;
    var copy = ().putAll(this).putAll(input);
    presets.put(name, copy);
    this.writeMinifiedTextArchive(savePath);
  }

  loadPreset { arg name;
    var preset = presets[name];
    this.putAll(preset);
    this.save;
    ^preset;
  }

  atAllPairs { arg ...keys;
    ^keys.collect({ arg key;
      var value = this[key];
      if (value.class == Env, {
        value = [value.asArray];
      });
      [key, value]
    }).flatten(1)
  }
  fromData {
    fromData.value(this)
  }

  toData {
    var newData = toData.value();
    ^newData;
  }

  save {
    var saveable = ().putAll(this).putAll((presets: this.presets));
    if (Project.current.notNil, {
      "saving to project".postln;
      Project.current[savePath.asSymbol] = saveable;
      Project.save;
      ^this;
    }, {
      saveable.writeMinifiedTextArchive(savePath);
    });
  }
  *serializeNodeValue { arg key, val;
    var x = case 
      { key.asString.contains("Env") } { Env.fromArray(val) }
      { key.asString.contains("eq_controls") } { val.clump(3).collect({ |item| [ item[0], item[1], item[2] ]}) }
      { val };
    ^x;
  }
  *serializeNode { arg node;
    var dataDict = Dictionary
      .newFrom(node.getKeysValues.flatten)
      .collect({ arg val, key;
        this.serializeNodeValue(key, val);
      });
    ^dataDict;
  }

  *deserializeNodeValue { arg key, val;
    var x = case 
        { key.asString.contains("Env") } { [val.toArray] }
        { key.asString.contains("eq_controls") } {
          val.collect({ |item,i|
            [ item[0], item[1], item[2] ]
          }).flat;
        }
        { key == 'presets' } { nil }
        { val };
    ^x;
  }

  *deserializeNode { arg params;
    ^params.collect({ arg val, key;
      this.deserializeNodeValue(key, val);
    })
  }
}

