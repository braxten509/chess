package dataaccess;

import model.AuthData;

public interface AuthDataAccess {

    public void createAuth(AuthData authData) throws DataAccessException;

}
