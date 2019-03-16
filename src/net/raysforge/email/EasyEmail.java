package net.raysforge.email;

import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.raysforge.easyswing.*;

public class EasyEmail implements ListSelectionListener {

	private static final String EASY_EMAIL_CFG = "cfg/test.cfg";

	private EasySwing es = new EasySwing("EasyEmail", 1920, 1080);;
	private EasySplitPane mainSplitPane = es.setSplitPaneAsMainContent(true, 400);
	private EasySplitPane msgSplitPane = new EasySplitPane(false, 400);
	private EasyTree folderTree = new EasyTree("root");
	private EasyList<EasyMsg> msgList = new EasyList<EasyMsg>(this);
	private EasyTextArea msgTextArea = new EasyTextArea();;
	// Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
	Connection conx = new Connection(es);

	public EasyEmail() {
		es.addToolBarItem("load", "load", (e) -> conx.load(EASY_EMAIL_CFG, msgList));
		es.addToolBarItem("createMail", "create", (e) -> createMail());
		es.addToolBarItem("delete", "delete", (e) -> msgList.getSelectedValue().delete());

		mainSplitPane.setLeft(folderTree);
		mainSplitPane.setRight(msgSplitPane);

		msgSplitPane.setTop(msgList);
		msgSplitPane.setBottom(msgTextArea);
		es.show();
	}

	private void createMail() {
		conx.create("Test", "Test User <test@test.net>", conx.cfg.user, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.");
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		new SwingWorker<String, String>() {
			public String doInBackground() {
				return msgList.get(msgList.getSelectedIndex()).cacheContent();
			}
			protected void done() {
				try {
					msgTextArea.setText(get());
					msgTextArea.setCaretPosition(0);
				} catch (Exception ignore) {
					System.out.println(ignore.getMessage());
				}
			}
		}.execute();
	}

	public static void main(String[] args) {
		new EasyEmail();
	}
}
