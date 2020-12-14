package client.model.entity;

import java.util.List;

import javax.swing.AbstractListModel;

import common.model.entity.User;

/**
 * 〈好友列表JList的Model〉<br>
 * 〈〉
 *
 */

public class MatesListModel extends AbstractListModel {
    private static final long serialVersionUID = -3903760573171074301L;
    private List<User> mateList;

    public MatesListModel(List<User> mateList) {
        this.mateList = mateList;
    }

    public MatesListModel() {

    }


    public void addElement(Object object) {
        if (mateList==null || mateList.contains(object)) {
            return;
        }
        int index = mateList.size();
        mateList.add((User)object);
        fireIntervalAdded(this, index, index);
    }

    public boolean removeElement(Object object) {
        int index = mateList.indexOf(object);
        if (index >= 0) {
            fireIntervalRemoved(this, index, index);
        }
        return mateList.remove(object);
    }

    public int getSize() {
        if(mateList==null) {
            return 0;
        }
        return mateList.size();
    }

    public Object getElementAt(int i) {
        return mateList.get(i);
    }

    public List<User> getmateList() {
        return mateList;
    }
}
