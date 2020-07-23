// Author: Benjamin Harris
// main class for the SpeciesListUpdater program
// July, 11 2020

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.net.URI;
import java.net.MalformedURLException;
import java.awt.Desktop;
import java.security.MessageDigest;
import javax.swing.JOptionPane;

public class SLUMain {
	public static void main(String[] args) {
		String url = "https://github.com/benjaminrayharris/SpeciesList/raw/master/sl.jar";
		String dir = getDir();
		String file = dir + File.separator + "sl.jar";
		String bupf = dir + File.separator + "bu.sl.jar";
		String tmpf = dir + File.separator + "temp.sl.jar";
		if (!ask("\n\nDo you want to upgrade?\n\n'Yes' to continue with upgrade\n\n")) {
			if (ask("\n\nDo you want to restore the backup?\n\n'Yes' to restore the previous backup\n\n'No' to quit\n\n")) {
				pop("The current program will become the backup.\n\nRun the restore process again to undo this restore\n\n");
				renameFile(file, tmpf);
				renameFile(bupf, file);
				renameFile(tmpf, bupf);
				openFile(file);
			} // end if
		} else {
			getFileFromURL(url, tmpf);
			if (!compareChecksums(tmpf)) {
				pop("ERROR: Downloaded file is corrupt.\n\nAborting upgrade process...");
			} else {
				backup(file, bupf);
				renameFile(tmpf, file);
				openFile(file);
			} // end if-else
		} // end if-else
	} // end main()

	private static void pop (String msg) {
		JOptionPane.showMessageDialog(null, msg, "Alert!", JOptionPane.INFORMATION_MESSAGE, null);
	} // end pop()

	private static boolean ask (String qstn) {
		int opt = JOptionPane.showConfirmDialog(null, qstn, "???????", JOptionPane.YES_NO_OPTION);
		return (opt == JOptionPane.YES_OPTION);
	} // end ask()

	private static int askif (String qstn) {
		return JOptionPane.showConfirmDialog(null, qstn);
	} // end askif()

	private static String getDir () {
		File f = null;
		String dirPath = null;
		try {
			f = new File(SLUMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			dirPath = f.toString();
		} catch (Exception e) {
			pop("setDir(): " + e);
		} // end try-catch
		return dirPath;
	} // end getDir()

	private static void getFileFromURL (String url, String file) {
		try {
			Files.deleteIfExists(Paths.get(file));
			InputStream in = URI.create(url).toURL().openStream();
			Files.copy(in, Paths.get(file));
			File f = new File(file);
			f.setExecutable(true);
		} catch (Exception e) {
		} // end try-catch
	} // end getFileFromURL()

	private static boolean compareChecksums (String path) {
		boolean isSame = false;
		try {
			String actual = getActualChecksum(path);
			String known = getKnownChecksum();
			//pop(actual + "\n" + known);
			return actual.equals(known);
		} catch (Exception e) {
		} // end try-catch
		return isSame;
	} // end compareChecksums()

	private static String getActualChecksum (String path) throws IOException {
		String str = "";
		try {
			File file = new File(path);
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//Get file input stream for reading the file content
			FileInputStream fis = new FileInputStream(file);

			//Create byte array to read data in chunks
			byte[] byteArray = new byte[1024];
			int bytesCount = 0;

			//Read file data and update in message digest
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			} // end while

			//close the stream; We don't need it now.
			fis.close();

			//Get the hash's bytes
			byte[] bytes = digest.digest();
			//This bytes[] has bytes in decimal format;
			//Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			} // end for

			//return complete hash
			str = sb.toString();
		} catch (Exception e) {
	
		} // end try-catch
		return str;
	} // end getActualChecksum()

	private static String getKnownChecksum () {
		String url = "https://github.com/benjaminrayharris/SpeciesList/raw/master/sl.jar.checksum";
		URL u;
		InputStream is = null;
		BufferedInputStream bis = null;
		BufferedReader d;
		String s = null;
		String str = null;
		try {
			u = new URL(url);
			is = u.openStream();
			bis = new BufferedInputStream(is);
			d = new BufferedReader(new InputStreamReader(bis));
			while ((s = d.readLine()) != null) {
				str = s;
			}  // end while
		} catch (EOFException eofe) {
			pop("checkVersion(): " + eofe);
		} catch (MalformedURLException mue) {
			pop("checkVersion(): " + mue);
		} catch (IOException ioe) {
			pop("checkVersion(): " + ioe);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			} // end try-catch
		} // end try-catch-catch-finally
		return str;		
	} // end getKnownChecksum()

	private static void backup (String file, String bupf) {
		String prmpt;
		int result;
		int yes = JOptionPane.YES_OPTION;
		int no = JOptionPane.NO_OPTION;
		prmpt = "\n\nBack up current version and overwrite the previously backed up version?";
		prmpt += "\n\n'Yes' to overwrite previous backup.";
		prmpt += "\n\n'No' to overwrite current program.";
		prmpt += "\n\n'Cancel' to abort upgrade process.\n\n";
		result = askif(prmpt);
		if (result == yes) {
			prmpt = "\n\nThis will overwrite the previous (backed up) version of the program!";
			prmpt += "\n\nLast chance to reconsider.";
			prmpt += "\n\nDo you want to abort upgrade?";
			prmpt += "\n\n'Yes' to cancel upgrade, and save BOTH the current version AND the backed up previous version";
			prmpt += "\n\n'No' to continue with upgrade process, lose the previous version, back up the current version, and install the new version\n\n";
			if (!ask(prmpt)) {
				//pop("!!!!!  after yes  !!!!!  result was no - continue with upgrade  !!!!!!!!!!");
				renameFile(file, bupf);
			} else {
				//pop("!!!!!  after yes  !!!!!  result was yes - abort upgrade  !!!!!!!!!!!!!!!!!");
				System.exit(0);
			} // end if-else
		} else if (result == no) {
			prmpt = "\n\nThis will overwrite the current version of the program!";
			prmpt += "\n\nDo you want to continue with upgrade?";
			prmpt += "\n\n'Yes' to continue with upgrade process, lose the current version while preserving the previous version, and install the new version";
			prmpt += "\n\n'No' to cancel upgrade, and save BOTH the current version AND the backed up previous version of program\n\n";
			if (ask(prmpt)) {
				//pop("!!!!!  after no   !!!!!  result was yes - continue with upgrade  !!!!!!!!!");
			} else {
				//pop("!!!!!  after no   !!!!!  result was no - abort upgrade  !!!!!!!!!!!!!!!!!!");
				System.exit(0);
			} // end if-else
		} else System.exit(0);
	} // end backup()

	private static void renameFile (String sour, String dest) {
		Path source = Paths.get(sour);
		Path destination = Paths.get(dest);
		try {
			Files.deleteIfExists(destination);
			Files.move(source, destination);
		} catch (IOException e) {
			pop("renameFile(): " + e);
		} // end try-catch
	} // end renameFile()

	private static void openFile (String file) {
		try {			
			Desktop.getDesktop().open(new File(file));
		} catch (Exception e) {
			pop("openUpdater(): " + e);
		} // end try-catch
	} // end openFile()

} // end class SLUMain
