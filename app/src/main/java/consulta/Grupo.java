package consulta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henrique on 25/05/2016.
 */
public class Grupo {
    public String string;
    public int status;
    public final List<Children> children = new ArrayList<Children>();

    public Grupo(String string) {
        this.string = string;
    }
    public int getStatus(){
        return this.status;
    }
    public void setStatus(int status){
        this.status = status;
    }

}
