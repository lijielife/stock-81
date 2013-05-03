package job.gf.stock;

import job.gf.simplejms.client.ITagSubscribeClient;
import job.gf.simplejms.client.SimpleTagSubscribeClient;
import job.gf.simplejms.server.TagSubscribeServer;
import job.gf.stock.msg.IMsgPublicer;
import job.gf.stock.msg.JmsMsgPublicer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonRunner {
	static Logger log = LoggerFactory.getLogger(DaemonRunner.class);
	static int code = 1000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// jmsServer
		log.info("jmsServer starts");
		TagSubscribeServer jmsServer = new TagSubscribeServer();
		boolean runSucess = jmsServer.startServer();

		if (!runSucess) {
			log.error("can not start jmsServer,exit");
			return;
		}
		log.info("jmsServer starts sucess");

		// jmsClient
		log.info("jmsClient starts");
		ITagSubscribeClient jmsClient = new SimpleTagSubscribeClient();
		jmsClient.start();
		log.info("jmsClient starts sucess");

		// publicer
		IMsgPublicer msgPublicer = new JmsMsgPublicer(jmsClient);

		// market
		IMarket market = new SimpleMarket();
		market.setIMsgPublicer(msgPublicer);
		market.init();
		market.run();
	}
}
