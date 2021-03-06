package restaurant.dao;

import java.security.MessageDigest;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import restaurant.model.User;

@Singleton
public class UserDAO {

    @PersistenceContext
    private EntityManager em;

    public void addUser(User user) {
        user.setPassword(getHashedPassword(user.getPassword()));
        em.persist(user);
    }

    public User validateUserCredentials(String userName, String password) {
        String txtQuery = "SELECT u FROM User u WHERE u.userName=:userName AND u.password=:password";
        TypedQuery<User> query = em.createQuery(txtQuery, User.class);
        query.setParameter("userName", userName);
        query.setParameter("password", getHashedPassword(password));
        return queryUser(query);
    }

    public Boolean userNameExists(String userName) {
        String txtQuery = "SELECT u FROM User u WHERE u.userName = :userName";
        TypedQuery<User> query = em.createQuery(txtQuery, User.class);
        query.setParameter("userName", userName);
        return queryUser(query) != null;
    }

    public boolean emailExists(String email) {
        String txtQuery = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> query = em.createQuery(txtQuery, User.class);
        query.setParameter("email", email);
        return queryUser(query) != null;
    }
    
    private User queryUser(TypedQuery<User> query) {
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public User findUserByName(String userName) {
        String txtQuery = "SELECT u FROM User u WHERE u.userName = :userName";
        TypedQuery<User> query = em.createQuery(txtQuery, User.class);
        query.setParameter("userName", userName);
        return queryUser(query);
    }
    
    private String getHashedPassword(String password) {
        try {
            MessageDigest mda = MessageDigest.getInstance("SHA-512");
            password = new String(mda.digest(password.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }
}