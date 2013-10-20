package de.uvwxy.xbee.commands;

import java.util.HashMap;

import de.uvwxy.xbee.apimode.messages.MessageID;

public enum ATCommand {
	CMD_A1 ("A1"),
	CMD_A2 ("A2"),
	CMD_AC ("AC"),
	CMD_AI ("AI"),
	CMD_AP ("AP"),
	CMD_AS ("AS"),
	CMD_AV ("AV"),
	CMD_BD ("BD"),
	CMD_CA ("CA"),
	CMD_CC ("CC"),
	CMD_CE ("CE"),
	CMD_CH ("CH"),
	CMD_CN ("CN"),
	CMD_CT ("CT"),
	CMD_D0 ("D0"),
	CMD_D1 ("D1"),
	CMD_D2 ("D2"),
	CMD_D3 ("D3"),
	CMD_D4 ("D4"),
	CMD_D5 ("D5"),
	CMD_D6 ("D6"),
	CMD_D7 ("D7"),
	CMD_D8 ("D8"),
	CMD_DA ("DA"),
	CMD_DB ("DB"),
	CMD_DH ("DH"),
	CMD_DL ("DL"),
	CMD_DN ("DN"),
	CMD_DP ("DP"),
	CMD_EA ("EA"),
	CMD_EC ("EC"),
	CMD_ED ("ED"),
	CMD_EE ("EE"),
	CMD_FP ("FP"),
	CMD_FR ("FR"),
	CMD_GT ("GT"),
	CMD_HV ("HV"),
	CMD_IA ("IA"),
	CMD_IC ("IC"),
	CMD_ID ("ID"),
	CMD_IO ("IO"),
	CMD_IR ("IR"),
	CMD_IS ("IS"),
	CMD_IT ("IT"),
	CMD_IU ("IU"),
	CMD_KY ("KY"),
	CMD_M0 ("M0"),
	CMD_M1 ("M1"),
	CMD_MM ("MM"),
	CMD_MY ("MY"),
	CMD_NB ("NB"),
	CMD_ND ("ND"),
	CMD_NI ("NI"),
	CMD_NT ("NT"),
	CMD_P0 ("P0"),
	CMD_P1 ("P1"),
	CMD_PL ("PL"),
	CMD_PR ("PR"),
	CMD_PT ("PT"),
	CMD_RE ("RE"),
	CMD_RN ("RN"),
	CMD_RO ("RO"),
	CMD_RP ("RP"),
	CMD_RR ("RR"),
	CMD_SC ("SC"),
	CMD_SD ("SD"),
	CMD_SH ("SH"),
	CMD_SL ("SL"),
	CMD_SM ("SM"),
	CMD_SP ("SP"),
	CMD_ST ("ST"),
	CMD_T0 ("T0"),
	CMD_T1 ("T1"),
	CMD_T2 ("T2"),
	CMD_T3 ("T3"),
	CMD_T4 ("T4"),
	CMD_T5 ("T5"),
	CMD_T6 ("T6"),
	CMD_T7 ("T7"),
	CMD_VL ("VL"),
	CMD_VR ("VR"),
	CMD_WR ("WR"),
	UNKNOWN ("UNKNOWN");
	
	public final String value;
	
	ATCommand(String v){
		this.value = v;
	}
	
	public static ATCommand get(String i) {
		ATCommand s = (ATCommand) reverseStatus.get(i);
		return (s == null) ? ATCommand.UNKNOWN : s;
	}

	private static final HashMap<String, ATCommand> reverseStatus = new HashMap<String, ATCommand>();
	static {
		for (ATCommand s : ATCommand.values()) {
			reverseStatus.put(s.value, s);
		}
	}
}
