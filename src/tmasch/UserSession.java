package tmasch;

public class UserSession {
    private static int id;
    private static String username;
    private static String fullName;
    private static int roleId;

    public static int getId() { return id; }
    public static void setId(int id) { UserSession.id = id; }

    public static String getUsername() { return username; }
    public static void setUsername(String username) { UserSession.username = username; }

    public static String getFullName() { return fullName; }
    public static void setFullName(String fullName) { UserSession.fullName = fullName; }

    public static int getRoleId() { return roleId; }
    public static void setRoleId(int roleId) { UserSession.roleId = roleId; }
    
    public static void clearSession() {
        id = 0;
        username = null;
        fullName = null;
        roleId = 0;
    }
}