package main;

import java.io.File;

import kisaragi.parser.LL1;
import kisaragi.parser.LR1;
import kisaragi.parser.SLR;
import kisaragi.util.IO;

public class Main {
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Error Input\nHow to use:\njava -jar <thisJAR path> <language path>");
		}
		else {
			LL1 ll1 = new LL1(args[0]);
			File f = new File(args[0]);
			
			String ff = ll1.displayFIRSTandFOLLOW();
			IO.writeText(ff, f.getParent() + File.separator + "FIRSTFOLLOW.txt", false, "utf-8");
			
			String ll1form = ll1.displayAnalysisForm();
			IO.writeText(ll1form, f.getParent() + File.separator + "LL1AnalysisForm.txt", false, "utf-8");
			
			SLR slr = new SLR(args[0]);
			if(slr.islegal()) {
				String items = slr.displayItems();
				String slrform = slr.displayAnalysisForm();
				IO.writeText(items, f.getParent() + File.separator + "SLRitems.txt", false, "utf-8");
				IO.writeText(slrform, f.getParent() + File.separator + "SLRAnalysisForm.md", false, "utf-8");
			}
			else {
				System.out.println("this language is not slr");
			}
			
			LR1 lr1 = new LR1(args[0]);
			if(lr1.islegal()) {
				String items = lr1.displayItems();
				String lr1form = lr1.displayAnalysisForm();
				IO.writeText(items, f.getParent() + File.separator + "LR1items.txt", false, "utf-8");
				IO.writeText(lr1form, f.getParent() + File.separator + "LR1AnalysisForm.md", false, "utf-8");
			}
			else {
				System.out.println("this language is not lr1");
			}
		}
	}
}
