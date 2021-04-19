package password.vault.command;

public enum CommandType {
    REGISTER,
    LOGIN,
    LOGOUT,
    RETRIEVE_CREDENTIALS,
    GENERATE_PASSWORD,
    ADD_PASSWORD,
    REMOVE_PASSWORD,
    DISCONNECT;

    public static boolean contains(String command) {
        for (CommandType c : CommandType.values()) {
            if (c.name().equals(command)) {
                return true;
            }
        }
        return false;
    }
}
