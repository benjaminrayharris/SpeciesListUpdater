// Author: Benjamin Harris
// Class for the SpeciesList Updater program
// July, 11 2020

//import java.awt.Dimension;
//import javax.swing.JFrame;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.io.IOException;
//import java.io.FileNotFoundException;
//import java.io.*;
import java.net.URL;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import org.apache.commons.io.FileUtils;

public class SLUMain {
	public static void main(String[] args) {
		
		/*
		JFrame win = new JFrame("Species List Manager");
		String exitQ = "Are you sure you want to exit?";
		win.setMinimumSize(new Dimension(800, 600));
		//win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		win.addWindowListener(
			new WindowAdapter() {
				@Override public void windowClosing(WindowEvent e) { 
					boolean result = UtilityShed.ask(exitQ);
					if (result) System.exit(0);
				}
			}
		);
		win.getContentPane();
		win.pack();
		win.setVisible(true);
		win.setLocationRelativeTo(null);
		//win.setIconImage(eng.sbsImage);
		*/

		
		popup("in main() about to set variables", null);
		//String url = "https://github.com/benjaminrayharris/SpeciesList/blob/master/sl.jar";
		String url = "https://benjaminharris.info/download/test.txt";
		
		String dir = getDir();
		
		//String file = dir + File.separator + "sl.jar";
		//String bupf = dir + File.separator + "bu.sl.jar";
		String file = dir + File.separator + "test.txt";
		String bupf = dir + File.separator + "bu.test.txt";
		
		

		popup("in main() verify file is there", null);
		//verify file is available to download

		if (ask("Should current version be backed up?\n"
				+ "And overwrite previously backed up version?")) {
			backupFile(file, bupf);
		} // end if
		
		//download new version
		getFileFromInternet(url, file);
		
		//start new version
		
		
	} // end main()
	
	private static String getDir () {
		File f = null;
		String dirPath = null;
		try {
			f = new File(SLUMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			dirPath = f.toString();
		} catch (Exception e) {
			popup("setDir(): " + e, null);
		} // end try-catch
		return dirPath;
	} // end setDir()
	
	private static void getFileFromInternet (String url, String path) {


		try {
			URL resource = new URL(url);
		
			File file = new File(path);
		
			FileUtils.copyURLToFile(resource, file);
		
			
			//String filename = "";
			//Response r = Jsoup.connect(url)
		    //.followRedirects(true) // follow redirects (it's the default)
			//    .ignoreContentType(true) // accept not just HTML
			//    .maxBodySize(10*1000*1000) // accept 10M bytes (default is 1M), or set to 0 for unlimited
			//    .execute(); // send GET request
			//FileOutputStream out = new FileOutputStream(new File(filename));
			//out.write(r.bodyAsBytes());
			//out.close();
			
		} catch (Exception e) {
			
		} // end try-catch-catch
	} // end getFileFromInternet()
	
	
	private static void popup (String msg, ImageIcon icon) {
		JOptionPane.showMessageDialog(null, msg, "Alert!", JOptionPane.INFORMATION_MESSAGE, icon);
	} // end popup()
	
	private static boolean ask (String qstn) {
		int opt = JOptionPane.showConfirmDialog(null, qstn, "???????", JOptionPane.YES_NO_OPTION);
		return (opt == JOptionPane.YES_OPTION);
	} // end ask()
	
	private static void backupFile (String file, String bupf) {
		
	} // end backupFile()
	
	private static String getin (String prmpt) {
		return JOptionPane.showInputDialog(prmpt);
	} // end getin()

	private static InputStream loadFile (String path) {
		InputStream in = SLUMain.class.getResourceAsStream(path);
		if (in == null) in = SLUMain.class.getResourceAsStream("/" + path);
		return in;
	} // end loadFile()
	
	// readFile() return value is non-null for success, null for failure
	private static String readFile (String path, boolean chooser) {
		String strToParse = "";
		File file;
		try {
			if (!chooser) {
				if (path == null) {
					popup("debug readFile(): 'path' can't be null", null); //////////////////////////
					return null;
				} // end if
				file = new File(path);
			} else {
				JFileChooser fc = new JFileChooser((path == null ? "ezezez" : path));
				fc.setAcceptAllFileFilterUsed(true);
				int result = fc.showDialog(null, "Load File");
				if (result == JFileChooser.APPROVE_OPTION) file = fc.getSelectedFile();
				else {
					popup("file load cancelled", null); /////////////////////////////////////////////////////////////////////////////
					return null;
				} // end if-else
			} // end if
			if (!file.exists()) {
				//popup(file.getName() + " does not exist", null);
				return null;
			} // end if
			BufferedReader in = new BufferedReader( new FileReader(file));
			String str = in.readLine();
			strToParse = str;
			str = in.readLine();
			while (str != null) {
				strToParse += "\n" + str;
				str = in.readLine();
			} // end while
			in.close();
		} catch (Exception e) { 
			popup("debug readFile(): " + e, null);
		} // end try-catch
		return strToParse;
	} // end readFile()

	// saveFile() return value is null for success, string message for failure reason
 	private static String saveFile (String st, String path, boolean chooser) {
 		File file;
		try {
			if (!chooser) {
				if (path == null) {
					return "save was aborted - no file path"; ////////////////////
				} // end if
				file = new File(path);
			} else {
				JFileChooser fc = new JFileChooser((path == null ? "ezezez" : path));
				fc.setAcceptAllFileFilterUsed(true);
				int result = fc.showDialog(null, "Save File");
				if ((result == JFileChooser.APPROVE_OPTION)) {
					String di = fc.getSelectedFile().getParentFile().toString();
					String name = fc.getSelectedFile().getName();
					String ext = fc.getFileFilter().getDescription();
					if (ext.equals("All Files")) ext = "";
					else ext = ext.substring(1);
					if (!name.endsWith(ext)) name = name + ext;
					name = di + File.separator + name;
					file = new File(name);
					popup("File saved as:  " + name, null);
				} else return "save was cancelled";
			} // end if
			if (file.getParentFile().exists()) {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				out.println(st);
				out.close();
			} else {
				popup("debug saveFile(): file's parents doesn't exist", null);
				return "problem with existence of file's 'parent'";
			} // end if-else
		} catch (Exception e) { 
			popup("debug saveFile(): " + e, null);
			return "problem in 'try': " + e;
		} // end try-catch
		return null;
	} // end saveFile()

} // end class SLUMain
