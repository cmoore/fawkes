package ivy.fawkes.loader;

import java.io.*;

/**
 * an instance of this class is created for every plugin (including the main cljminecraft one) that depends on cljminecraft, because
 * it will have to have in its plugin.yml the following:<br>
 * "main: cljminecraft.ClojurePlugin"
 *
 */
public class ClojurePlugin extends BasePlugin {
	
    private final static String selfCoreScript=selfPluginName+".core";
    private final static String selfEnableFunction="on-enable";
    private final static String selfDisableFunction="on-disable";
	
    private boolean loadClojureFile(String cljFile) {
        try {
						info( "About to load clojure file: " + cljFile );
            assert clojure.lang.Compiler.LOADER.isBound();
            clojure.lang.RT.loadResourceScript( cljFile );
            return true;
        } catch ( Exception e ) {
            severe( "Something broke setting up Clojure" );
            e.printStackTrace();
            return false;
        }
    }
    
    
    public final boolean loadClojureNameSpace( String ns ) {
        //String cljFile = ns.replaceAll( "[.]", "/" ) + ".clj";
        String cljFile = "ivy/fawkes/core.clj";
        return loadClojureFile( cljFile );
    }
    
    public Object invokeClojureFunction(String ns, String funcName) {
        return clojure.lang.RT.var("ivy.fawkes.core", "on-enable").invoke(this);
    }

    @Override
    public boolean start() {
        
        String pluginName = getDescription().getName();
        
        boolean success = false;
        if ( selfPluginName.equals( pluginName ) ) {
            info( "Enabling main " + pluginName + " clojure Plugin" );
            success = loadClojureNameSpace(selfCoreScript);
        } else {
            info( "Enabling child " + pluginName + " clojure Plugin" );
            success = loadClojureNameSpace(pluginName+".core");
        }
        
        loadClojureNameSpace ("cljminecraft.core");
        invokeClojureFunction(selfCoreScript, selfEnableFunction );
        
        return success;
    }

    @Override
    public void stop() {//called only when onEnable didn't fail (if we did the logic right)
        String pluginName = getDescription().getName();
        if ( selfPluginName.equals( pluginName ) ) {
            info( "Disabling main " + pluginName + " clojure Plugin" );
        } else {
            info( "Disabling child " + pluginName + " clojure Plugin" );
        }
        invokeClojureFunction( selfCoreScript, selfDisableFunction );
    }
    

/*in plugin.yml of your clojure plugin which depends on cljminecraft, these are required:
 * 
 * main: cljminecraft.ClojurePlugin
 * depend: [cljminecraft]
 * 
 * and the name of your plugin(in your plugin.yml) should be the ns name of core.clj and core.clj should be the main script 
 * which includes the two methods start and stop which take plugin instance as parameter
 * 
  */  
}
