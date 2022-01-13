# Import statement for supercollider

this repository contains utilities helpful for modularising and importing code in supercollider projects,
and for managing / saving projects using those code modules  

## What is a module?
a module is at its simplest a `.scd` file, associated with an [Environment](./classes/Import/Import.sc#L65) stored in memory and indexed by its path.  
A module's path uniquely identifies it and as such only one instance of a module is loaded at a time.

resources and values can be declared and instantiated and values may be 'exported' or exposed to consumers of the module using the 
`~` environment variable syntax shortcut

### Module lifecycle
#### 1. Importing  
typically a module is imported by using some variation of Import's constructor or an extension added to Symbol:  
```supercollider
var module = Import(
  path: "/path/to/module",
  // path to the module.scd file - the extension is unnecessary

  expose: false,
  // whether to expose the returned module in the currentEnvironment
  // as ~module (the module's filename)
);

'./relativeModule'.import; // creates the environment variable ~relativeModule

~renamedModule  = './otherRelativeModule'.asModule; // doesn't create an extra variable, just returns the module
```   
Import will then try to resolve the supplied path either by looking up an absolute path, resolving it relative to the current file  
or searching for it in a configurable list of default module directories 
#### 2. Loading 
once a module's path has been found an already loaded instance of it is returned, or it must be freshly loaded.
when initially loaded, a Module creates an environment, and its supercollider script file is loaded within it.
#### custom loaders
custom loaders can be specified at import time:  
`Import("Clap.wav", loader: "soundfile")`
will search for a soundfile at the path "Clap.wav", and create an environment bearing that path.   
Then a script called "soundfile.scd" is found and its code is executed inside the "Clap.wav" module - see [here](./examples/clapModule.scd#30) and [here](./examples/soundfile.scd) for an example

### Module functions
functions behave in a specific way when saved in an Environment:
```supercollider
var a = (
  a: 1,
  func: { arg env, extra;
    [env.a, extra].postln;
  }
);
a.func(2); // -> [1, 2];
```
prints two values (rather than `[2, nil]`) because when called in this way, 
the parent environment is passed in as an implicit first parameter, similar to how `self` is by convention the first parameter of a python class instance method and refers to the instance itself.
In this way, functions declared and exposed from a module may refer to values within the module by using the first parameter.

because it might be tedious to specify this first parameter all the time, the helper class `ModFunc` or `M` for short is provided to allow a function to execute within the context of its own module:

```supercollider
// someModule.scd
~moduleVariable = 1;
~exposedFunction = M { arg extra;
  [~moduleVariable, extra].postln;
};


// someConsummer.scd
'someModule'.import;
~someModule.exposedFunction(2); // -> [1, 2]
```
### Document support