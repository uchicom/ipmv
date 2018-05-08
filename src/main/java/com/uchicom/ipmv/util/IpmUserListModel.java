package com.uchicom.ipmv.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.uchicom.ipm.Ipm;
import com.uchicom.ipm.dto.User;

public class IpmUserListModel implements ListModel<User> {

	private List<ListDataListener> listenerList = new ArrayList<>();

	private Ipm messanger;

	public IpmUserListModel(Ipm messanger) {
		this.messanger = messanger;
		messanger.addUserListener(event -> {
			listenerList.forEach(listener -> {
				switch (event.getType()) {
				case CHANGED:
					listener.contentsChanged(new ListDataEvent(event.getUsers(), ListDataEvent.CONTENTS_CHANGED,
							event.getStart(), event.getEnd()));
					break;
				case ADDED:
					listener.contentsChanged(new ListDataEvent(event.getUsers(), ListDataEvent.INTERVAL_ADDED,
							event.getStart(), event.getEnd()));
					break;
				case REMOVED:
					listener.contentsChanged(new ListDataEvent(event.getUsers(), ListDataEvent.INTERVAL_REMOVED,
							event.getStart(), event.getEnd()));
					break;
				case CLEARED:
					listener.contentsChanged(new ListDataEvent(event.getUsers(), ListDataEvent.INTERVAL_REMOVED,
							event.getStart(), event.getEnd()));
					break;
				}
			});
		});
	}

	@Override
	public int getSize() {
		return messanger.getUserList().size();
	}

	@Override
	public User getElementAt(int index) {
		return messanger.getUserList().get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listenerList.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(l);
	}

}
