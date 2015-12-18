/**
 * 
 */
package tech.bcozo.game.assetfilter;

import java.io.PrintWriter;
import java.util.Calendar;

import com.badlogic.gdx.backends.gwt.preloader.DefaultAssetFilter;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: AssetFilter
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 17, 2015 9:54:41 PM
 */
public class AssetFilter extends DefaultAssetFilter {
    private static final int IGNORE_LIST_INDEX_FULL_NAME = 0;
    private static final int IGNORE_LIST_INDEX_EXTENSION = 1;
    private static final int IGNORE_LIST_INDEX_DIRECTORY = 2;
    private String[][] ignoreList = { { ".DS_Store" }, { ".svn" }, { ".svn" } };
    private PrintWriter out;

    /**
     * <p>
     * This is the constructor of AssetFilter
     * </p>
     */
    public AssetFilter() {
        try {
            out = new PrintWriter("AssetFilter.log");
            System.out.println("**AssetFilter Log file created.**");
            out.println("**********************");
            Calendar calendar = Calendar.getInstance();
            out.println("Log Time: " + calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DATE) + " "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE) + ":"
                    + calendar.get(Calendar.SECOND));
            out.println("**********************");
            out.flush();
        } catch (Exception ex) {
            System.out.println("**Cannot create AssetFilter Log file.**");
        }
    }

    private void writeLog(String text) {
        if (out != null) {
            out.println(text);
            out.flush();
        }
    }

    private void writeLog(String text, boolean newLine) {
        if (out != null) {
            if (newLine)
                out.println(text);
            else
                out.print(text);
            out.flush();
        }
    }

    private String extension(String file) {
        String name = file;
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex <= 0)
            return "";
        return name.substring(dotIndex + 1);
    }

    @Override
    public boolean accept(String file, boolean isDirectory) {
        // check for ignored directory
        if (isDirectory) {
            for (String dirname : ignoreList[IGNORE_LIST_INDEX_DIRECTORY]) {
                if (file.endsWith(dirname)) {
                    writeLog("Filter directory: " + file + " [ignore]", true);
                    return false;
                }
            }
        } else {
            // check for ignored extension
            String fileext;
            for (String ext : ignoreList[IGNORE_LIST_INDEX_EXTENSION]) {
                fileext = extension(file);
                if (fileext != "") {
                    if (ext.endsWith(fileext)) {
                        writeLog("Filter by extension: " + file + " [ignore]",
                                true);
                        return false;
                    }
                } else
                    return false;
            }
            // check for ignored full name
            for (String fullname : ignoreList[IGNORE_LIST_INDEX_FULL_NAME]) {
                if (file.trim().endsWith(fullname)) {
                    writeLog("Filter by name: " + file + " [ignore]", true);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public AssetType getType(String file) {
        String extension = extension(file).toLowerCase();
        if (isImage(extension))
            return AssetType.Image;
        if (isAudio(extension))
            return AssetType.Audio;
        if (isText(extension))
            return AssetType.Text;
        return AssetType.Binary;
    }

    private boolean isImage(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg")
                || extension.equals("png") || extension.equals("bmp")
                || extension.equals("gif");
    }

    private boolean isText(String extension) {
        return extension.equals("json") || extension.equals("xml")
                || extension.equals("txt") || extension.equals("glsl")
                || extension.equals("fnt") || extension.equals("pack")
                || extension.equals("obj") || extension.equals("atlas")
                || extension.equals("g3dj");
    }

    private boolean isAudio(String extension) {
        return extension.equals("mp3") || extension.equals("ogg")
                || extension.equals("wav");
    }

    @Override
    public String getBundleName(String file) {
        return "assets";
    }

}
