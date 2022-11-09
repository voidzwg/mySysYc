package IR;

import IR.Values.User;
import IR.Values.Value;

public class Use {
    private Value value;
    private User user;
    private int pos;

    public Use(Value value, User user, int pos) {
        this.value = value;
        this.user = user;
        this.pos = pos;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
