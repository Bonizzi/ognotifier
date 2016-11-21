package ognotifier;

import java.io.IOException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class PrjHub_Parser extends SwingWorker<Integer, Integer> {

	int old_issues = 0;
	int counter = 1;
	HtmlPage prj_issues = null;
	int final_total_issues;
	final WebClient webClient = new WebClient(BrowserVersion.CHROME);

	public void doParse() {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

		try {
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

			System.out.println(
					"=== STARTED... OPENING PRJHUB AND SEARCHING FOR OPENED ISSUES (TAKES A WHILE, APROX. 30 seconds)... ");

			HtmlPage html_page = null;
			int PAGE_RETRY = 10;
			try {
				html_page = webClient.getPage("http://www.prjhub.com/#/login");
				Thread.sleep(4000);

			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; !html_page.asXml().contains("Signup") && i < PAGE_RETRY; i++) {
				try {
					System.out.println("*** SEARCHING FOR LOGIN BUTTON. ATTEMPT N.: +++ " + i + " +++ ***");
					Thread.sleep(1000 * (i + 1));
					html_page = webClient.getPage("http://www.prjhub.com/#/login");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			if (html_page.asXml().contains("api/v1/github/login")) {
				System.out.println("*** FOUND GITHUB LOGIN BUTTON ***");
				final HtmlAnchor anchor = html_page.getAnchorByHref("api/v1/github/login");
				// System.out.println(anchor.asXml());
				final HtmlPage page2 = anchor.click();
				// System.out.println(page2.asXml());
				Thread.sleep(3000);

				/* REDIRECTED TO GITHUB LOGIN PAGE; STARTING LOGIN PHASE */

				final HtmlForm form = (HtmlForm) page2.querySelector("form");
				final HtmlSubmitInput button = (HtmlSubmitInput) form.getInputsByValue("Sign in").get(0);
				final HtmlTextInput textField = form.getInputByName("login");
				textField.setValueAttribute("user");
				final HtmlPasswordInput textField2 = form.getInputByName("password");
				textField2.setValueAttribute("pass");
				HtmlPage git_clicked = button.click();
				Thread.sleep(3000);

				// Document blocker_check = Jsoup.parse(git_clicked.asXml());
				// String blocker_detected =
				// blocker_check.select(".btn").toString();
				// System.out.println(blocker_detected);
				// String blocker_compare = "Authorize application";
				if (git_clicked.asXml().contains("Authorize application")) {
					System.out.println("*** AUTH BLOCK DETECTED ***");
					final HtmlForm auth_form = (HtmlForm) git_clicked
							.getFirstByXPath("//form[@action='/login/oauth/authorize']");
					// System.out.println(auth_form);

					final HtmlButton auth_button = (HtmlButton) auth_form
							.getFirstByXPath("//*[@id='js-pjax-container']/div[1]/div/div[2]/div/div[1]/form/p/button");
					// System.out.println(auth_button);
					/* HtmlPage auth_clicked = */
					auth_button.click();
					Thread.sleep(3000);
					parsePrj();
				} else {
					System.out.println("*** NOT LOGGED IN; SO DONE IT, AND NO AUTH BLOCK ***");
					parsePrj();
				}
			} else {
				System.out.println("*** ALREADY LOGGED IN ***");
				parsePrj();
			}
		} catch (Exception e) {
			System.err.println("*** CATCHED EXCEPTION. ERROR IN PAGE PARSING ***");
			System.err.println();
			System.err.println(e.toString());
			doParse();
		} finally {
			this.webClient.close();
		}
	}

	public void parsePrj() {

		try {
			prj_issues = webClient.getPage("http://www.prjhub.com/#/issues?q=is:open%20client:%22_my%22&page=1");
			Thread.sleep(6000);
			String issuesAsXml = prj_issues.asXml();
			// System.out.println(issuesAsXml); // prints out prjhub source code
			// for debugging...
			if (prj_issues.asXml().contains("Total issues")) {
				Document total_issues = Jsoup.parse(issuesAsXml);
				String tot = total_issues.select(".ng-binding").get(3).toString();
				System.out.println(tot);
				System.out.println(tot.substring(46, 48));
				this.final_total_issues = Integer.parseInt(tot.substring(46, 48));
				System.out.println("*** TOTAL OPENED ISSUES: " + final_total_issues);

				System.out.println("=== FINISHED.");
				// webClient.close();
			} else {
				webClient.close();
				doParse();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void parsePrj2() {

		try {
			prj_issues.refresh();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String issuesAsXml = prj_issues.asXml();

		if (prj_issues.asXml().contains("Total issues")) {
			Document total_issues = Jsoup.parse(issuesAsXml);
			String tot = total_issues.select(".ng-binding").get(3).toString();
			System.out.println(tot);
			System.out.println(tot.substring(46, 48));
			this.final_total_issues = Integer.parseInt(tot.substring(46, 48));
			System.out.println("*** TOTAL OPENED ISSUES: " + final_total_issues);

			System.out.println("=== FINISHED.");

			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			done();

			// webClient.close();
		} else {
			webClient.close();
			doParse();
		}

	}

	/*
	 * public void doParse() {
	 * 
	 * System.out.println("doParse()");
	 * 
	 * try { Thread.sleep(4000); } catch (InterruptedException e) {
	 * e.printStackTrace(); } i++; System.out.println(i);
	 * 
	 * }
	 */

	@Override
	protected Integer doInBackground() throws Exception {
		System.out.println("doInBackground()");

		doParse();
		done();
		return this.final_total_issues;

	}

	@Override
	protected void done() {
		try {



			// if(counter==3){ // for debug
			// old_issues=81;
			// }
			System.out.println("done() counter: *** " + counter);

			SwingAppLauncher.label3.setText("<html>Issues totali: <font color='red'><strong>" + final_total_issues
					+ "</strong></font>. <br /> Updating every minute.</html>");

			if (counter != 1 && old_issues < final_total_issues) {
				SendTicketAlert.send();
				SwingAppLauncher.doAlarm();
			}
			old_issues = final_total_issues;
			
			
			counter++;
			
			
			SwingAppLauncher.label3.validate();
			SwingAppLauncher.label3.repaint();
			parsePrj2();
			// webClient.close();

		} catch (Exception ignore) {
		}
	}

	public int getResult() {
		return this.final_total_issues;
	}
}