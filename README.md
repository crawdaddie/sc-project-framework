# import statement for supercollider

this repository contains utilities helpful for modularising and importing code in supercollider projects,
and for managing / saving projects using those code modules  

## What is a module?
a module is at its simplest a `.scd` file, associated with an [Environment](./classes/Import/Import.sc#L65) stored in memory and indexed by its path.  
A module's path uniquely identifies it and as such only one instance of a module is loaded at a time.

resources and values can be declared and instantiated and values may be 'exported' or exposed to consumers of the module using the 
`~` environment variable syntax shortcut

### Module lifecycle
1. Importing  
    typically a module is imported by using some variation of Import's constructor or an extension added to Symbol:  
    ```supercollider
    var module = Import(
      path: "/path/to/module",
      // path to the module.scd file - the extension is unnecessary

      expose: false,
      // whether to expose the returned module in the currentEnvironment as ~module (the module's filename)
    )

    './relativeModule'.import; // creates the environment variable ~relativeModule

    ~renamedModule  = './otherRelativeModule'.asModule; // doesn't create an extra variable, just returns the module
    ```   
    Import will then try to resolve the supplied path either by looking up an absolute path, resolving it relative to the current file, or searching for it in a configurable list of default module directories 
2. Loading
    once a module's path has been found an already loaded instance of it is returned, or it must be freshly loaded