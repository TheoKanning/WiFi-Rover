package theokanning.rover.user;

import com.quickblox.users.model.QBUser;

/**
 * Enum of all QuickBlox users for this application
 */
public enum User {
    DRIVER("Driver", "qh8ZhC5z", 7348049),
    ROBOT("Robot", "hxJVfsSd", 7348068);

    private final String name;
    private final int id;
    private final QBUser qbUser;

    User(String name, String password, int id) {
        this.name = name;
        this.id = id;
        this.qbUser = new QBUser(name, password);
        qbUser.setId(id);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public QBUser getQbUser() {
        return qbUser;
    }

    @Override
    public String toString() {
        return name;
    }
}
