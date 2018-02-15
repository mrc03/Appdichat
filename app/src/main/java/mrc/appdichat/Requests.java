package mrc.appdichat;

/**
 * Created by HP on 11-02-2018.
 */

public class Requests {

    String request_type;

    public Requests() {
    }

    public Requests(String request_type) {

        this.request_type = request_type;
    }


    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }


}
