Project {
	classvar <recentProjects;
	classvar recentProjectsFilePath;
	classvar emptyProjectDir;
	classvar <>defaultProjectsDir = "/Users/adam/projects/sc/projects";
  classvar <>current;


	classvar <projectFile, <projectDir, <srcDir, <synthsFile, <saveDir, <dataDir;
  classvar <>canvas;
  classvar <timingContextGui;
	classvar <player;
	classvar <assetView;
  classvar <gitClient;

  *initClass {
    Class.initClassTree(Import);
    Import.search = [
      [{Project.srcDir.notNil},  { Project.srcDir  }], 
      [{Project.dataDir.notNil}, { Project.dataDir }],
    ] ++ Import.search;
  }

  *load { arg path;
    this.setPaths(path);
    current = path !? path.load ?? ( randSeed: 1001 );
    thisThread.randSeed = current.randSeed ?? 1001;
    synthsFile.load;
  }

  *loadFromGit { arg path, branchName;
    var localPath = path ? thisProcess.nowExecutingPath.dirname;
    var gitRoot; 

    gitClient = Git(localPath);
    gitRoot = gitClient.git(["rev-parse", "--show-toplevel"]);
    "root: %".format(gitRoot).postln;
    gitClient.localPath = gitRoot;

    if(Git.isGit(localPath).not, {
      gitClient.git(["init"]);
    });

    if (branchName.notNil, { gitClient.git(["checkout", branchName]) });
    gitClient.git(["status"]).postln;
    this.load(gitClient.localPath +/+ "index.scproj");
  }

  *randSeed { arg seed;
    current.randSeed = seed;
    thisThread.randSeed = seed;
  }

	*setPaths { arg path;
		projectFile = path;
		saveDir = projectFile.dirname;
		projectDir = projectFile.dirname;
		srcDir = projectDir +/+ "src";
    synthsFile = srcDir +/+ "synths.scd";
		dataDir = projectDir +/+ "data";
    format("cd %", projectDir).unixCmd;
    gitClient = Git(projectDir);
	}

	*initFromProjectFile { arg path;
		
		if (path.pathMatch.size != 1) {
			Error("project file % does not exist".format(path)).throw;
		};
		this.setPaths(path);
		this.load(path);
		this.setRecents(path);
	}
	
	*save { arg payload, commit = true;
    current.writeMinifiedTextArchive(projectFile);
    if (commit, {
      this.commit
    });
	}

  *commit {
    var commitMsg = format("'%'", Date.getDate);
    var gitOutput;
    gitOutput = this.gitClient.git(["add", "."]);
    gitOutput.postln;
    gitOutput = this.gitClient.git(["commit", "-m", commitMsg]);
    gitOutput.postln;
  }

  *openPath { arg path;
  }
	
	*open { arg payload;
	}
}

ProjectKeyActionManager {
  *new {
    ^super.new();
  }

  *keyDownAction { arg view, char, modifiers, unicode, keycode, key;
		switch ([modifiers, key]) 
      { [ 262144, 83 ] } { Project.save } // ctrl-s
    ;
  }

}
