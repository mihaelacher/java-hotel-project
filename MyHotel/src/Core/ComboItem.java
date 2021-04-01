package Core;

public class ComboItem {
	
	private String id;
    private String label;

    public ComboItem(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return label;
    }
}
