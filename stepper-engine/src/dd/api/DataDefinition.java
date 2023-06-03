package dd.api;

public interface DataDefinition
{
    String getName();
    boolean isUserFriendly();
    Class<?> getType();
}
