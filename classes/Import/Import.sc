Import {
	classvar <> defaultModulePath;
  classvar <> defaultSoundfileLibPath;
  classvar <> search;
	*initClass {
		defaultModulePath = "/Users/adam/projects/sc/ecosystem/core-modules";
    defaultSoundfileLibPath = "/Users/adam/projects/sc/lib";
    search = [
      [{true}, { this.defaultModulePath }],
      [{true}, { this.defaultSoundfileLibPath }],
    ];
	}
	// this class contains utilities for resolving a symbol into a path
	// used for loading an instance of Mod (module)
 	*new { arg module, expose = true, loader;
		var mod;
		var modPath = this.resolvePath(module);

		mod = Mod.new(modPath, loader);

		if (expose, { currentEnvironment.put(this.getModuleVarname(module), mod) });

		^mod;
	}

	*getModuleVarname { arg module;
		var modString = module.asString;
    var components = modString.split($/);
    var name = components.last.asSymbol;
    if (name == 'index.scd', {
      name = components[components.size -2].asSymbol;
    });
    ^name;
	}

	*resolvePath { arg module;
    var path;
    var moduleString = module.asString;
    var pathMatch = (moduleString ++ "*" ).pathMatch;
    if (pathMatch.isEmpty) {
      var cwd = thisProcess.nowExecutingPath !? (_.dirname) ?? "./";
      // matches paths relative to the current file 
      pathMatch = (cwd +/+ moduleString ++ "*").pathMatch;
    };

    search.do { arg condPath;
      var condition, checkPath;
      #condition, checkPath = condPath;
      if (pathMatch.isEmpty && condition.value()) { pathMatch = (checkPath.value() +/+ moduleString ++ "*").pathMatch };
    };

    format("module: %", pathMatch[0]).postln;
    path = pathMatch[0];
    if (path.endsWith("/"), {
      path = path +/+ "index.scd";
      if (path.pathMatch.isEmpty, {
        Error("could not resolve module: % - does not contain an index.scd file".format(module)).throw
      })
    })

		^path;
	}
}


Mod : Environment {
	classvar <>all;
  var <loaderPath;

	*initClass {
		all = IdentityDictionary.new();
	}
  *switchToMod { arg path;
    var mod = all.at(path.asSymbol);
    if (mod.notNil, {
      format("switching to environment %", mod).postln;
      mod.push;
    }, {
      currentEnvironment = topEnvironment
    });
  }

	*new { arg path, loader;
		// check if module already exists and return that, or make a new one
 
		var mod = all.at(path.asSymbol) !? { arg existingModule;
			existingModule;
			} ?? {
				super.new.init(path, loader);
			};

		^mod;
	}

  *startUp { arg interpreter;
    interpreter.preProcessor = { arg code;
      Mod.preprocessor(code);
    }
  }

  *preprocessor { arg code;
    if (currentEnvironment.class == Mod) {
      code = "var mod = currentEnvironment;\n" ++ code;
      ^code;
    };
    ^code;
  }

	*newVirtual { arg path, extra;
		var mod = all.at(path.asSymbol) !? { arg module;
			format("module % already exists (passing previously loaded module)", path.split($/).last).postln;
			module;
			} ?? {
				super.new.init(path).loadVirtual(extra);
			};
		^mod;
	}

	*newWithinEnv { arg path, env; 
		^super.new.init(path).loadWithinEnv(env);
	}
//   at { arg key ... args;
//     [key, args].postln;
//     try {
// 
// 
// 
//     } { |error|
//       ^super.at(key, *args);
//     }
  // }


	init { arg path, loader;

		this.proto_((
			path: path,
			open: #{ arg ev; Document.open(ev[\path]) };
		));

		know = true;

		this.registerModule;
    if (loader.notNil, {
      // specifying a loader in the import is useful for 
      // loading a soundfile as a module (for example)
      this.withLoader(loader);
      ^this;
    });

		if (PathName.new(path).isFolder) {
			this.loadFromFolder(path)
		} {
     	this.loadFromPath;
      // TODO: define default per-extension loaders
			// var ext = path.splitext.last;
			// switch (ext,
			// 	"wav", { this.loadFromSoundfile },
			// 	"mp3", { this.loadFromSoundfile },
			// 	{ this.loadFromPath }
			// );
		};

		^this
	}

	loadFromFolder { arg path;
		^this.make {
			var indexPath = ~path +/+ "index.scd";
			if (indexPath.pathMatch.size == 1) {
				var mod = super.new(indexPath);
				currentEnvironment.put(~path.basename.splitext[0].asSymbol, mod);
			} {
				(~path +/+ "*.scd").pathMatch.do({ arg fullPath;
					var mod = Mod(fullPath);
					currentEnvironment.put(fullPath.basename.splitext[0].asSymbol, mod);
				});
			}
		};
	}

	loadFromPath {
		^this.make { ~path.load };
	}

  withLoader { arg loader;
    loaderPath = Import.resolvePath(loader);
    this.make {
      loaderPath.load;
    }
  }

	loadVirtual { arg extra;
		^this.make { extra.value() };
	}

	loadWithinEnv { arg env;
	 	this.parent_(env);
	 	this.use { ~path.load };
	 	^this
	}

	registerModule {
		var symbol = this.at(\path).asSymbol;
		all.put(symbol, this);
	}

	reload {
		^this.loadFromPath
	}

	*reloadOnSave { arg path;
    path.postln;
		all.at(path.asSymbol) !? { arg module;
			"reloading on save: %".format(module).postln;
			fork {
				module.reload;
				Dispatcher(
					type: Topics.moduleReload, 
					payload: (
						path: path
					)
				);
			}	
		}
	}

	printModule {
		this.asString.postln;
		this.order.do({ arg key;
			if ((key != 'open') && (key != 'path')) {
				var keyString = key.asString;
				format("%: %", keyString, this.at(key).cs).postln;
			}
		});
	}

// 	asString {
// 		^format("Module %", this.at(\path).basename);
// 	}
// 
//   asString { 
//     var path = this.at(\path);
//     var components = path.split($/);
//     var name = components.last.asSymbol;
//     if (name == 'index.scd', {
//       name = components[components.size -2].asSymbol;
//     });
//     ^name;
// 	}
// 
// 
	asModule {
		^this
	}

  ref {
    ^(
      path: this['path'],
      loaderPath: loaderPath
    )
  }
}

ModFunc {
	*new { arg function;
		^{ arg ev ... args;
			ev.use {
				function.valueArray(args);
			}
		}
	}
}
DataValue {
  *new { arg key, object;
    currentEnvironment.put(key, object);
    // ^object;
    ^object
  }
}
D : DataValue {}


M : ModFunc {}

ModObject {
	*new { arg function;
		^{ arg ev ... args;
			ev.use {
				Environment.make({
					function.valueArray(args);
				})
				.know_(true);
			}
		}
	}
}

O : ModObject {}

ModValue {
  *new { arg key, value;
    currentEnvironment.put(key, value);
  }
}

V : ModValue {}

+ String {

  loadSoundfile {
    // ensures soundfile is loaded
    // but returns string
    this.asSoundfileMod;
    ^this;
  }

	asSoundfileMod {
		var mod = Mod.all.at(this.asSymbol);
    if (mod.isNil) {
      mod = Import(this, false);
    };
    ^mod;
	}
}

+ Association {
  export {
    [this.key, this.value].postln;
    currentEnvironment.put(this.key, this.value);
    // var oldKeys, newKeys;
    // oldKeys = currentEnvironment.keys;
    // currentEnvironment.use { this.value };
    // newKeys = currentEnvironment.keys.select({ arg k; oldKeys.includes(k).not });
    // currentEnvironment.exports !? {
    //   currentEnvironment.exports
    // };
  }
}


+ Symbol {
  from { arg aModule;
    var mod = aModule.asModule;
    ^mod.at(this);
  }
}
