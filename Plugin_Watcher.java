import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ij.IJ;
import ij.Menus;
import ij.plugin.PlugIn;
/**
 *  The "Plugin_Watcher" PlugIn watches a specified directory for modified .class files
 *  and will automatically run the "Refresh Menus" command. 
 * @author Michael Schmidt
 *
 */
public class Plugin_Watcher implements PlugIn {
	
	@Override
	public void run(String arg0) {
		Path dir = Paths.get(IJ.getDirectory(""));
		
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();

		    WatchKey key = dir.register(watcher,
		                           ENTRY_CREATE,
		                           ENTRY_DELETE,
		                           ENTRY_MODIFY);    
		    
		    for (;;) {
		        
		        try {
		            key = watcher.take();
		        } catch (InterruptedException x) {
		        	IJ.log("Error: " + x.getMessage());
		            return;
		        }

		        for (WatchEvent<?> event: key.pollEvents()) {
		            WatchEvent.Kind<?> kind = event.kind();

		            if (kind == OVERFLOW) {
		                continue;
		            }

		            WatchEvent<Path> ev = (WatchEvent<Path>)event;
		            Path filename = ev.context();
		            
					Matcher m = Pattern.compile("\\.class$").matcher(filename.getFileName().toString());
					
					if (m.find() && kind == ENTRY_MODIFY) {
						Menus.updateImageJMenus();
					    continue;
					}
		        }

		        boolean valid = key.reset();
		        if (!valid) {
		            break;
		        }
		    }
		} catch (IOException x) {
		   IJ.log("Error: " + x.getMessage());
		}

	}

}
