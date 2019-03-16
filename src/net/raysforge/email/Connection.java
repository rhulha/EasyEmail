package net.raysforge.email;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.*;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.raysforge.easyswing.EasyList;
import net.raysforge.easyswing.EasySwing;

public class Connection {

	public Configuration cfg;
	private Session session;
	private Store msgStore;
	private Folder msgFolder;

	public Connection(EasySwing es) {
		es.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					if (msgFolder != null) {
						msgFolder.close(true);
					}
				} catch (MessagingException e1) {
				}
				try {
					if (msgStore != null) {
						msgStore.close();
					}
				} catch (MessagingException e1) {
				}
			}
		});
	}

	public void create(String subject, String from, String to, String body) {
		try {
			Message[] messages = new Message[1];
			messages[0] = new EasyMsg(subject, from, to, body).getMsg();
			msgFolder.appendMessages(messages);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(String configFile, EasyList<EasyMsg> msgList) {
		new Thread() {

			public void run() {
				try {
					cfg = new Configuration(configFile);
					Properties props = new Properties();
					props.put("mail.imap.connectiontimeout", 5000);
					props.put("mail.imap.timeout", 5000);
					props.put("mail.imaps.connectiontimeout", 2000);
					props.put("mail.imaps.timeout", 2000);
					if( cfg.root_cert_file != null )
						props.put("mail.imaps.ssl.socketFactory", Trust.getTrustedCertificateMailSSLSocketFactory(cfg.root_cert_file, props));

					session = Session.getDefaultInstance(props);
					msgStore = session.getStore("imaps");
					msgStore.connect(cfg.server, cfg.user, cfg.pw);
					msgFolder = msgStore.getFolder("INBOX");
					msgFolder.open(Folder.READ_WRITE);
					System.out.println("getting emails from the last month only!");
					Calendar cal = Calendar.getInstance();
					cal.roll(Calendar.MONTH, false);
					Message[] search = msgFolder.search(new ReceivedDateTerm(ComparisonTerm.GT, cal.getTime())); // new FlagTerm(new Flags(Flags.Flag.DRAFT), true));//
					for (Message msg : search) {
						System.out.print('.');
						EasyMsg em = new EasyMsg(msg);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								msgList.addElement(em);
								// msgList.repaint();
							}
						});
					}
					Thread.yield();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage(), e.getMessage(), JOptionPane.ERROR_MESSAGE);
				}
			}

		}.start();
	}
}
