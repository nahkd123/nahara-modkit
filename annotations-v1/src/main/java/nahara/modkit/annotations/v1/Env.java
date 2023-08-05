package nahara.modkit.annotations.v1;

public enum Env {
	ALL("*"),
	CLIENT("client"),
	SERVER("server");

	private String configName;

	private Env(String configName) {
		this.configName = configName;
	}

	public String getConfigName() {
		return configName;
	}
}
