package dd.api;

public abstract class AbstractDataDefinition implements DataDefinition
{
    private final String name;
    private final boolean userFriendly;
    private final Class<?> type;

    protected AbstractDataDefinition(String name, boolean userFriendly, Class<?> type)
    {
        this.name = name;
        this.userFriendly = userFriendly;
        this.type = type;
    }

    @Override
    public boolean isUserFriendly() {
        return userFriendly;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
