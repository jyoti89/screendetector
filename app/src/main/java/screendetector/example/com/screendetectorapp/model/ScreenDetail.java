package screendetector.example.com.screendetectorapp.model;

/**
 * Created by Ayaz  0/23/17.
 */

public class ScreenDetail {
    private long id;
    private String fileName;
    private String path;

    public ScreenDetail(long id, String fileName, String path) {
        this.id = id;
        this.fileName = fileName;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }
}
