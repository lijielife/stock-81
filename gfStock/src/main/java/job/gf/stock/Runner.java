package job.gf.stock;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import job.gf.simplejms.client.ITagSubscribeClient;
import job.gf.simplejms.client.SimpleTagSubscribeClient;
import job.gf.simplejms.server.TagSubscribeServer;
import job.gf.stock.generate.CrazyTradeInfoGenerator;
import job.gf.stock.generate.ITradeInfoGenerator;
import job.gf.stock.generate.RandomTradeInfoGenerator;
import job.gf.stock.generate.TenTradeInfoGenerator;
import job.gf.stock.msg.IMsgPublicer;
import job.gf.stock.msg.JmsMsgPublicer;

public class Runner {
	static Logger log = LoggerFactory.getLogger(Runner.class);
	static int code = 1000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean daemon = false;
		if (args != null && args.length > 1) {
			if ("daemon".equals(args[0])) {
				daemon = true;
			}
		}

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

		if (!daemon) {
			// command
			Scanner scan = new Scanner(System.in);
			String line = null;
			String commands[] = null;
			printTip();
			do {
				boolean done = false;
				line = scan.nextLine().trim();
				if (line.length() == 0)
					continue;
				commands = line.split("\\s+", 2);

				if (commands.length == 1) {
					if ("help".equals(commands[0])) {
						printTip();
						done = true;
					} else if ("quit".equals(commands[0])) {
						System.out.println("market stops");
						market.destroy();
						jmsClient.destroy();
						System.out.println("jmsClient stops");
						jmsServer.stopServer();
						System.out.println("jmsServer stops");
						System.out.println("quit sucess");
						done = true;
						break;
					}
				} else if (commands.length == 2) {
					if ("broadcast".equals(commands[0])) {
						market.broadcastNotice(commands[1]);
						printDone();
						done = true;
					} else if ("addStock".equals(commands[0])) {
						boolean result = parseAddStock(market, commands[1]);
						if (!result) {
							System.out.println("wrong param");
						} else {
							printDone();
						}
						done = true;
					}
				}

				if (!done) {
					System.out.println("unknow command");
					printTip();
				}

			} while (true);
		}

	}

	private static void printDone() {
		System.out.println("done");
	}

	private static void printTip() {
		StringBuilder sb = new StringBuilder();
		sb.append("command help:\n");
		sb.append("help\n");
		sb.append("  --show help\n");

		sb.append("broadcast msg\n");
		sb.append("  --broadcast msg,ex:broadcast hello\n");

		sb.append("addStock name price(unit:yuan) total(>100) [random|crazy|ten]\n");
		sb.append("  --ex:addStock gf 10.8 100000\n");
		sb.append("  --ex:addStock haha 22.3 100000 crazy\n");
		sb.append("  --random,generate data is random\n");
		sb.append("  --crazy,generate data is crazy,up and down\n");
		sb.append("  --ten,generate data is random in ten pencent\n");

		sb.append("quit\n");
		sb.append("  --quit the application\n\n");

		sb.append("\n");
		System.out.println(sb.toString());
	}

	private static boolean parseAddStock(IMarket market, String param) {
		String params[] = param.split("\\s");
		if (params.length < 3) {
			return false;
		}

		String name = params[0].trim();
		double price = 0;
		int total = 0;
		try {
			price = Double.parseDouble(params[1]);
			total = Integer.parseInt(params[2]);
		} catch (Exception e) {
			return false;
		}
		if (total < 100)
			total = 100;

		int priceInt = (int) (price * 100);
		if (total <= 0)
			return false;

		String generator = "random";
		if (params.length > 3)
			generator = params[3];
		ITradeInfoGenerator tradeGenerator = null;
		if ("crazy".equals(generator)) {
			tradeGenerator = new CrazyTradeInfoGenerator();
		} else if ("ten".equals(generator)) {
			tradeGenerator = new TenTradeInfoGenerator();
		} else {
			tradeGenerator = new RandomTradeInfoGenerator();
		}

		code++;

		Stock stock = new Stock(String.valueOf(code), name, total, priceInt,
				tradeGenerator);
		stock.setUpdateTime(1000);
		market.addStock(stock);
		return true;
	}

}
