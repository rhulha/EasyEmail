package net.raysforge.email;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.imap.IMAPBodyPart;

public class EasyMsg {

	private Message msg;
	private Address address;
	private String subject;
	private String contentCache;
	private Date receivedDate;
	private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm");

	public EasyMsg(Message msg) {
		this.msg = msg;
		try {
			address = msg.getFrom()[0];
			subject = msg.getSubject();
			receivedDate = msg.getReceivedDate();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public EasyMsg(String subject, String from, String to, String body) throws MessagingException {
		msg = new MimeMessage((Session) null);
		msg.setSubject(subject);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		if ((msg.getAllRecipients() == null) || (msg.getAllRecipients().length == 0)) {
			throw new MessagingException("No valid recipients");
		}
		msg.setSentDate(new java.util.Date());
		msg.setText(body);
	}

	public Message getMsg() {
		return msg;
	}

	public String cacheContent() {
		try {
			if (contentCache == null)
				contentCache = "" + recurse(msg.getContent());
			return contentCache;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return sdf.format(receivedDate) + "   -   " + subject + "   -   " + address;
	}

	public void delete() {
		try {
			msg.setFlag(Flags.Flag.DELETED, true);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object recurse(Object o) {
		try {
			if (o instanceof MimeMultipart) {
				MimeMultipart mmp = (MimeMultipart) o;
				IMAPBodyPart ibp = (IMAPBodyPart) mmp.getBodyPart(0);
				return recurse(ibp.getContent());
			} else {
				return o;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
