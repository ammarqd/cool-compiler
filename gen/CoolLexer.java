// Generated from //wsl.localhost/Ubuntu/home/ammar-q/Cool-Compiler/src/CoolLexer.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CoolLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PERIOD=1, COMMA=2, AT=3, SEMICOLON=4, COLON=5, CURLY_OPEN=6, CURLY_CLOSE=7, 
		PARENT_OPEN=8, PARENT_CLOSE=9, PLUS_OPERATOR=10, MINUS_OPERATOR=11, MULT_OPERATOR=12, 
		DIV_OPERATOR=13, INT_COMPLEMENT_OPERATOR=14, LESS_OPERATOR=15, LESS_EQ_OPERATOR=16, 
		EQ_OPERATOR=17, ASSIGN_OPERATOR=18, RIGHTARROW=19, CLASS=20, INHERITS=21, 
		IF=22, THEN=23, ELSE=24, FI=25, WHILE=26, LOOP=27, POOL=28, LET=29, IN=30, 
		CASE=31, OF=32, ESAC=33, NEW=34, ISVOID=35, NOT=36, TRUE=37, FALSE=38, 
		TYPEID=39, OBJECTID=40, WHITESPACE=41, INT_CONST=42, STR_CONST=43, LINE_COMMENT=44, 
		BEGIN_COMMENT=45, END_COMMENT=46, COMMENT_TEXT=47, BEGIN_INNER_COMMENT=48, 
		ERROR_UNCLOSED_COMMENT=49, ERROR=50;
	public static final int
		STRING_MODE=1, COMMENT_MODE=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "STRING_MODE", "COMMENT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"PERIOD", "COMMA", "AT", "SEMICOLON", "COLON", "CURLY_OPEN", "CURLY_CLOSE", 
			"PARENT_OPEN", "PARENT_CLOSE", "PLUS_OPERATOR", "MINUS_OPERATOR", "MULT_OPERATOR", 
			"DIV_OPERATOR", "INT_COMPLEMENT_OPERATOR", "LESS_OPERATOR", "LESS_EQ_OPERATOR", 
			"EQ_OPERATOR", "ASSIGN_OPERATOR", "RIGHTARROW", "CLASS", "INHERITS", 
			"IF", "THEN", "ELSE", "FI", "WHILE", "LOOP", "POOL", "LET", "IN", "CASE", 
			"OF", "ESAC", "NEW", "ISVOID", "NOT", "TRUE", "FALSE", "TYPEID", "OBJECTID", 
			"CHAR", "WHITESPACE", "INT_CONST", "DIGIT", "BEGIN_STRING", "STR_TEXT", 
			"STR_ESC", "STR_INVALID", "UNTERMINATED_STRING", "NULL_STRING", "ESC_NULL", 
			"EOF_STRING", "STR_CONST", "LINE_COMMENT", "BEGIN_COMMENT", "EOF_COMMENT", 
			"END_COMMENT", "COMMENT_TEXT", "BEGIN_INNER_COMMENT", "ERROR_UNCLOSED_COMMENT", 
			"UNMATCHED_PAREN", "ERROR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'.'", "','", "'@'", "';'", "':'", "'{'", "'}'", "'('", "')'", 
			"'+'", "'-'", "'*'", "'/'", "'~'", "'<'", "'<='", "'='", "'<-'", "'=>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PERIOD", "COMMA", "AT", "SEMICOLON", "COLON", "CURLY_OPEN", "CURLY_CLOSE", 
			"PARENT_OPEN", "PARENT_CLOSE", "PLUS_OPERATOR", "MINUS_OPERATOR", "MULT_OPERATOR", 
			"DIV_OPERATOR", "INT_COMPLEMENT_OPERATOR", "LESS_OPERATOR", "LESS_EQ_OPERATOR", 
			"EQ_OPERATOR", "ASSIGN_OPERATOR", "RIGHTARROW", "CLASS", "INHERITS", 
			"IF", "THEN", "ELSE", "FI", "WHILE", "LOOP", "POOL", "LET", "IN", "CASE", 
			"OF", "ESAC", "NEW", "ISVOID", "NOT", "TRUE", "FALSE", "TYPEID", "OBJECTID", 
			"WHITESPACE", "INT_CONST", "STR_CONST", "LINE_COMMENT", "BEGIN_COMMENT", 
			"END_COMMENT", "COMMENT_TEXT", "BEGIN_INNER_COMMENT", "ERROR_UNCLOSED_COMMENT", 
			"ERROR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	    int commentLevel = 0;
	    int stringLength = 0;


	public CoolLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CoolLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 44:
			BEGIN_STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 45:
			STR_TEXT_action((RuleContext)_localctx, actionIndex);
			break;
		case 46:
			STR_ESC_action((RuleContext)_localctx, actionIndex);
			break;
		case 47:
			STR_INVALID_action((RuleContext)_localctx, actionIndex);
			break;
		case 48:
			UNTERMINATED_STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 49:
			NULL_STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 50:
			ESC_NULL_action((RuleContext)_localctx, actionIndex);
			break;
		case 51:
			EOF_STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 52:
			STR_CONST_action((RuleContext)_localctx, actionIndex);
			break;
		case 54:
			BEGIN_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 55:
			EOF_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 56:
			END_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 58:
			BEGIN_INNER_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 59:
			ERROR_UNCLOSED_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 60:
			UNMATCHED_PAREN_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void BEGIN_STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 stringLength = 0; 
			break;
		}
	}
	private void STR_TEXT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			 stringLength++; 
			break;
		}
	}
	private void STR_ESC_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			 stringLength++; 
			break;
		}
	}
	private void STR_INVALID_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:
			 stringLength++; 
			break;
		}
	}
	private void UNTERMINATED_STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:
			 setText("Unterminated string constant"); 
			break;
		}
	}
	private void NULL_STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:
			 setText("String contains null character."); 
			break;
		}
	}
	private void ESC_NULL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 6:
			 setText("String contains escaped null character."); 
			break;
		}
	}
	private void EOF_STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 7:
			 setText("EOF in string constant"); 
			break;
		}
	}
	private void STR_CONST_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 8:

			    if (stringLength > 1024) {
			        setText("String constant too long");
			        setType(ERROR);
			    }

			break;
		}
	}
	private void BEGIN_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 9:
			 commentLevel++; 
			break;
		}
	}
	private void EOF_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 10:
			 setText("EOF in comment"); 
			break;
		}
	}
	private void END_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 11:

			        commentLevel--;
			    
			break;
		}
	}
	private void BEGIN_INNER_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 12:

			        commentLevel++;
			    
			break;
		}
	}
	private void ERROR_UNCLOSED_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 13:

			        commentLevel--;
			        if (commentLevel > 0) {
			            setText("EOF in comment");
			            setType(ERROR);
			        } else {
			            skip();
			        }
			    
			break;
		}
	}
	private void UNMATCHED_PAREN_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 14:
			 setText("Unmatched *)"); 
			break;
		}
	}

	public static final String _serializedATN =
		"\u0004\u00002\u01a3\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff"+
		"\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002"+
		"\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005"+
		"\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002"+
		"\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002"+
		"\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010"+
		"\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013"+
		"\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016"+
		"\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019"+
		"\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c"+
		"\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f"+
		"\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002#\u0007"+
		"#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002(\u0007"+
		"(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002-\u0007"+
		"-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u00022\u0007"+
		"2\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u00027\u0007"+
		"7\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002<\u0007"+
		"<\u0002=\u0007=\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001 \u0001"+
		" \u0001 \u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001\"\u0001\"\u0001"+
		"\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0001$"+
		"\u0001$\u0001$\u0001$\u0001$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001"+
		"%\u0001&\u0001&\u0005&\u0108\b&\n&\f&\u010b\t&\u0001\'\u0001\'\u0005\'"+
		"\u010f\b\'\n\'\f\'\u0112\t\'\u0001(\u0001(\u0001)\u0004)\u0117\b)\u000b"+
		")\f)\u0118\u0001)\u0001)\u0001*\u0004*\u011e\b*\u000b*\f*\u011f\u0001"+
		"+\u0001+\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001-\u0001-\u0001"+
		"-\u0001-\u0001-\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001/\u0001"+
		"/\u0001/\u0001/\u0001/\u0001/\u00010\u00010\u00010\u00010\u00010\u0001"+
		"0\u00011\u00011\u00051\u0143\b1\n1\f1\u0146\t1\u00011\u00011\u00011\u0001"+
		"1\u00011\u00011\u00012\u00012\u00012\u00012\u00052\u0152\b2\n2\f2\u0155"+
		"\t2\u00012\u00012\u00012\u00012\u00012\u00012\u00013\u00013\u00013\u0001"+
		"3\u00013\u00013\u00014\u00014\u00014\u00014\u00014\u00015\u00015\u0001"+
		"5\u00015\u00055\u016c\b5\n5\f5\u016f\t5\u00015\u00015\u00016\u00016\u0001"+
		"6\u00016\u00016\u00016\u00016\u00016\u00017\u00017\u00017\u00017\u0001"+
		"7\u00017\u00018\u00018\u00018\u00018\u00018\u00018\u00018\u00018\u0001"+
		"9\u00019\u00019\u00019\u0001:\u0001:\u0001:\u0001:\u0001:\u0001:\u0001"+
		":\u0001:\u0001;\u0001;\u0001;\u0001;\u0001;\u0001;\u0001<\u0001<\u0001"+
		"<\u0001<\u0001<\u0001<\u0001<\u0001=\u0001=\u0002\u0144\u0153\u0000>\u0003"+
		"\u0001\u0005\u0002\u0007\u0003\t\u0004\u000b\u0005\r\u0006\u000f\u0007"+
		"\u0011\b\u0013\t\u0015\n\u0017\u000b\u0019\f\u001b\r\u001d\u000e\u001f"+
		"\u000f!\u0010#\u0011%\u0012\'\u0013)\u0014+\u0015-\u0016/\u00171\u0018"+
		"3\u00195\u001a7\u001b9\u001c;\u001d=\u001e?\u001fA C!E\"G#I$K%M&O\'Q("+
		"S\u0000U)W*Y\u0000[\u0000]\u0000_\u0000a\u0000c\u0000e\u0000g\u0000i\u0000"+
		"k+m,o-q\u0000s.u/w0y1{\u0000}2\u0003\u0000\u0001\u0002\u001a\u0002\u0000"+
		"CCcc\u0002\u0000LLll\u0002\u0000AAaa\u0002\u0000SSss\u0002\u0000IIii\u0002"+
		"\u0000NNnn\u0002\u0000HHhh\u0002\u0000EEee\u0002\u0000RRrr\u0002\u0000"+
		"TTtt\u0002\u0000FFff\u0002\u0000WWww\u0002\u0000OOoo\u0002\u0000PPpp\u0002"+
		"\u0000VVvv\u0002\u0000DDdd\u0002\u0000UUuu\u0001\u0000AZ\u0001\u0000a"+
		"z\u0004\u000009AZ__az\u0002\u0000\t\r  \u0001\u000009\u0005\u0000\u0000"+
		"\u0000\n\n\r\r\"\"\\\\\t\u0000\n\n\r\r\"\"\\\\bbffnnrrtt\u0002\u0000\n"+
		"\n\"\"\u0002\u0000\n\n\r\r\u01a5\u0000\u0003\u0001\u0000\u0000\u0000\u0000"+
		"\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000"+
		"\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r"+
		"\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001"+
		"\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000"+
		"\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000"+
		"\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/"+
		"\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u00003\u0001\u0000"+
		"\u0000\u0000\u00005\u0001\u0000\u0000\u0000\u00007\u0001\u0000\u0000\u0000"+
		"\u00009\u0001\u0000\u0000\u0000\u0000;\u0001\u0000\u0000\u0000\u0000="+
		"\u0001\u0000\u0000\u0000\u0000?\u0001\u0000\u0000\u0000\u0000A\u0001\u0000"+
		"\u0000\u0000\u0000C\u0001\u0000\u0000\u0000\u0000E\u0001\u0000\u0000\u0000"+
		"\u0000G\u0001\u0000\u0000\u0000\u0000I\u0001\u0000\u0000\u0000\u0000K"+
		"\u0001\u0000\u0000\u0000\u0000M\u0001\u0000\u0000\u0000\u0000O\u0001\u0000"+
		"\u0000\u0000\u0000Q\u0001\u0000\u0000\u0000\u0000U\u0001\u0000\u0000\u0000"+
		"\u0000W\u0001\u0000\u0000\u0000\u0000[\u0001\u0000\u0000\u0000\u0000m"+
		"\u0001\u0000\u0000\u0000\u0000o\u0001\u0000\u0000\u0000\u0000{\u0001\u0000"+
		"\u0000\u0000\u0000}\u0001\u0000\u0000\u0000\u0001]\u0001\u0000\u0000\u0000"+
		"\u0001_\u0001\u0000\u0000\u0000\u0001a\u0001\u0000\u0000\u0000\u0001c"+
		"\u0001\u0000\u0000\u0000\u0001e\u0001\u0000\u0000\u0000\u0001g\u0001\u0000"+
		"\u0000\u0000\u0001i\u0001\u0000\u0000\u0000\u0001k\u0001\u0000\u0000\u0000"+
		"\u0002q\u0001\u0000\u0000\u0000\u0002s\u0001\u0000\u0000\u0000\u0002u"+
		"\u0001\u0000\u0000\u0000\u0002w\u0001\u0000\u0000\u0000\u0002y\u0001\u0000"+
		"\u0000\u0000\u0003\u007f\u0001\u0000\u0000\u0000\u0005\u0081\u0001\u0000"+
		"\u0000\u0000\u0007\u0083\u0001\u0000\u0000\u0000\t\u0085\u0001\u0000\u0000"+
		"\u0000\u000b\u0087\u0001\u0000\u0000\u0000\r\u0089\u0001\u0000\u0000\u0000"+
		"\u000f\u008b\u0001\u0000\u0000\u0000\u0011\u008d\u0001\u0000\u0000\u0000"+
		"\u0013\u008f\u0001\u0000\u0000\u0000\u0015\u0091\u0001\u0000\u0000\u0000"+
		"\u0017\u0093\u0001\u0000\u0000\u0000\u0019\u0095\u0001\u0000\u0000\u0000"+
		"\u001b\u0097\u0001\u0000\u0000\u0000\u001d\u0099\u0001\u0000\u0000\u0000"+
		"\u001f\u009b\u0001\u0000\u0000\u0000!\u009d\u0001\u0000\u0000\u0000#\u00a0"+
		"\u0001\u0000\u0000\u0000%\u00a2\u0001\u0000\u0000\u0000\'\u00a5\u0001"+
		"\u0000\u0000\u0000)\u00a8\u0001\u0000\u0000\u0000+\u00ae\u0001\u0000\u0000"+
		"\u0000-\u00b7\u0001\u0000\u0000\u0000/\u00ba\u0001\u0000\u0000\u00001"+
		"\u00bf\u0001\u0000\u0000\u00003\u00c4\u0001\u0000\u0000\u00005\u00c7\u0001"+
		"\u0000\u0000\u00007\u00cd\u0001\u0000\u0000\u00009\u00d2\u0001\u0000\u0000"+
		"\u0000;\u00d7\u0001\u0000\u0000\u0000=\u00db\u0001\u0000\u0000\u0000?"+
		"\u00de\u0001\u0000\u0000\u0000A\u00e3\u0001\u0000\u0000\u0000C\u00e6\u0001"+
		"\u0000\u0000\u0000E\u00eb\u0001\u0000\u0000\u0000G\u00ef\u0001\u0000\u0000"+
		"\u0000I\u00f6\u0001\u0000\u0000\u0000K\u00fa\u0001\u0000\u0000\u0000M"+
		"\u00ff\u0001\u0000\u0000\u0000O\u0105\u0001\u0000\u0000\u0000Q\u010c\u0001"+
		"\u0000\u0000\u0000S\u0113\u0001\u0000\u0000\u0000U\u0116\u0001\u0000\u0000"+
		"\u0000W\u011d\u0001\u0000\u0000\u0000Y\u0121\u0001\u0000\u0000\u0000["+
		"\u0123\u0001\u0000\u0000\u0000]\u0129\u0001\u0000\u0000\u0000_\u012e\u0001"+
		"\u0000\u0000\u0000a\u0134\u0001\u0000\u0000\u0000c\u013a\u0001\u0000\u0000"+
		"\u0000e\u0140\u0001\u0000\u0000\u0000g\u014d\u0001\u0000\u0000\u0000i"+
		"\u015c\u0001\u0000\u0000\u0000k\u0162\u0001\u0000\u0000\u0000m\u0167\u0001"+
		"\u0000\u0000\u0000o\u0172\u0001\u0000\u0000\u0000q\u017a\u0001\u0000\u0000"+
		"\u0000s\u0180\u0001\u0000\u0000\u0000u\u0188\u0001\u0000\u0000\u0000w"+
		"\u018c\u0001\u0000\u0000\u0000y\u0194\u0001\u0000\u0000\u0000{\u019a\u0001"+
		"\u0000\u0000\u0000}\u01a1\u0001\u0000\u0000\u0000\u007f\u0080\u0005.\u0000"+
		"\u0000\u0080\u0004\u0001\u0000\u0000\u0000\u0081\u0082\u0005,\u0000\u0000"+
		"\u0082\u0006\u0001\u0000\u0000\u0000\u0083\u0084\u0005@\u0000\u0000\u0084"+
		"\b\u0001\u0000\u0000\u0000\u0085\u0086\u0005;\u0000\u0000\u0086\n\u0001"+
		"\u0000\u0000\u0000\u0087\u0088\u0005:\u0000\u0000\u0088\f\u0001\u0000"+
		"\u0000\u0000\u0089\u008a\u0005{\u0000\u0000\u008a\u000e\u0001\u0000\u0000"+
		"\u0000\u008b\u008c\u0005}\u0000\u0000\u008c\u0010\u0001\u0000\u0000\u0000"+
		"\u008d\u008e\u0005(\u0000\u0000\u008e\u0012\u0001\u0000\u0000\u0000\u008f"+
		"\u0090\u0005)\u0000\u0000\u0090\u0014\u0001\u0000\u0000\u0000\u0091\u0092"+
		"\u0005+\u0000\u0000\u0092\u0016\u0001\u0000\u0000\u0000\u0093\u0094\u0005"+
		"-\u0000\u0000\u0094\u0018\u0001\u0000\u0000\u0000\u0095\u0096\u0005*\u0000"+
		"\u0000\u0096\u001a\u0001\u0000\u0000\u0000\u0097\u0098\u0005/\u0000\u0000"+
		"\u0098\u001c\u0001\u0000\u0000\u0000\u0099\u009a\u0005~\u0000\u0000\u009a"+
		"\u001e\u0001\u0000\u0000\u0000\u009b\u009c\u0005<\u0000\u0000\u009c \u0001"+
		"\u0000\u0000\u0000\u009d\u009e\u0005<\u0000\u0000\u009e\u009f\u0005=\u0000"+
		"\u0000\u009f\"\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005=\u0000\u0000"+
		"\u00a1$\u0001\u0000\u0000\u0000\u00a2\u00a3\u0005<\u0000\u0000\u00a3\u00a4"+
		"\u0005-\u0000\u0000\u00a4&\u0001\u0000\u0000\u0000\u00a5\u00a6\u0005="+
		"\u0000\u0000\u00a6\u00a7\u0005>\u0000\u0000\u00a7(\u0001\u0000\u0000\u0000"+
		"\u00a8\u00a9\u0007\u0000\u0000\u0000\u00a9\u00aa\u0007\u0001\u0000\u0000"+
		"\u00aa\u00ab\u0007\u0002\u0000\u0000\u00ab\u00ac\u0007\u0003\u0000\u0000"+
		"\u00ac\u00ad\u0007\u0003\u0000\u0000\u00ad*\u0001\u0000\u0000\u0000\u00ae"+
		"\u00af\u0007\u0004\u0000\u0000\u00af\u00b0\u0007\u0005\u0000\u0000\u00b0"+
		"\u00b1\u0007\u0006\u0000\u0000\u00b1\u00b2\u0007\u0007\u0000\u0000\u00b2"+
		"\u00b3\u0007\b\u0000\u0000\u00b3\u00b4\u0007\u0004\u0000\u0000\u00b4\u00b5"+
		"\u0007\t\u0000\u0000\u00b5\u00b6\u0007\u0003\u0000\u0000\u00b6,\u0001"+
		"\u0000\u0000\u0000\u00b7\u00b8\u0007\u0004\u0000\u0000\u00b8\u00b9\u0007"+
		"\n\u0000\u0000\u00b9.\u0001\u0000\u0000\u0000\u00ba\u00bb\u0007\t\u0000"+
		"\u0000\u00bb\u00bc\u0007\u0006\u0000\u0000\u00bc\u00bd\u0007\u0007\u0000"+
		"\u0000\u00bd\u00be\u0007\u0005\u0000\u0000\u00be0\u0001\u0000\u0000\u0000"+
		"\u00bf\u00c0\u0007\u0007\u0000\u0000\u00c0\u00c1\u0007\u0001\u0000\u0000"+
		"\u00c1\u00c2\u0007\u0003\u0000\u0000\u00c2\u00c3\u0007\u0007\u0000\u0000"+
		"\u00c32\u0001\u0000\u0000\u0000\u00c4\u00c5\u0007\n\u0000\u0000\u00c5"+
		"\u00c6\u0007\u0004\u0000\u0000\u00c64\u0001\u0000\u0000\u0000\u00c7\u00c8"+
		"\u0007\u000b\u0000\u0000\u00c8\u00c9\u0007\u0006\u0000\u0000\u00c9\u00ca"+
		"\u0007\u0004\u0000\u0000\u00ca\u00cb\u0007\u0001\u0000\u0000\u00cb\u00cc"+
		"\u0007\u0007\u0000\u0000\u00cc6\u0001\u0000\u0000\u0000\u00cd\u00ce\u0007"+
		"\u0001\u0000\u0000\u00ce\u00cf\u0007\f\u0000\u0000\u00cf\u00d0\u0007\f"+
		"\u0000\u0000\u00d0\u00d1\u0007\r\u0000\u0000\u00d18\u0001\u0000\u0000"+
		"\u0000\u00d2\u00d3\u0007\r\u0000\u0000\u00d3\u00d4\u0007\f\u0000\u0000"+
		"\u00d4\u00d5\u0007\f\u0000\u0000\u00d5\u00d6\u0007\u0001\u0000\u0000\u00d6"+
		":\u0001\u0000\u0000\u0000\u00d7\u00d8\u0007\u0001\u0000\u0000\u00d8\u00d9"+
		"\u0007\u0007\u0000\u0000\u00d9\u00da\u0007\t\u0000\u0000\u00da<\u0001"+
		"\u0000\u0000\u0000\u00db\u00dc\u0007\u0004\u0000\u0000\u00dc\u00dd\u0007"+
		"\u0005\u0000\u0000\u00dd>\u0001\u0000\u0000\u0000\u00de\u00df\u0007\u0000"+
		"\u0000\u0000\u00df\u00e0\u0007\u0002\u0000\u0000\u00e0\u00e1\u0007\u0003"+
		"\u0000\u0000\u00e1\u00e2\u0007\u0007\u0000\u0000\u00e2@\u0001\u0000\u0000"+
		"\u0000\u00e3\u00e4\u0007\f\u0000\u0000\u00e4\u00e5\u0007\n\u0000\u0000"+
		"\u00e5B\u0001\u0000\u0000\u0000\u00e6\u00e7\u0007\u0007\u0000\u0000\u00e7"+
		"\u00e8\u0007\u0003\u0000\u0000\u00e8\u00e9\u0007\u0002\u0000\u0000\u00e9"+
		"\u00ea\u0007\u0000\u0000\u0000\u00eaD\u0001\u0000\u0000\u0000\u00eb\u00ec"+
		"\u0007\u0005\u0000\u0000\u00ec\u00ed\u0007\u0007\u0000\u0000\u00ed\u00ee"+
		"\u0007\u000b\u0000\u0000\u00eeF\u0001\u0000\u0000\u0000\u00ef\u00f0\u0007"+
		"\u0004\u0000\u0000\u00f0\u00f1\u0007\u0003\u0000\u0000\u00f1\u00f2\u0007"+
		"\u000e\u0000\u0000\u00f2\u00f3\u0007\f\u0000\u0000\u00f3\u00f4\u0007\u0004"+
		"\u0000\u0000\u00f4\u00f5\u0007\u000f\u0000\u0000\u00f5H\u0001\u0000\u0000"+
		"\u0000\u00f6\u00f7\u0007\u0005\u0000\u0000\u00f7\u00f8\u0007\f\u0000\u0000"+
		"\u00f8\u00f9\u0007\t\u0000\u0000\u00f9J\u0001\u0000\u0000\u0000\u00fa"+
		"\u00fb\u0005t\u0000\u0000\u00fb\u00fc\u0007\b\u0000\u0000\u00fc\u00fd"+
		"\u0007\u0010\u0000\u0000\u00fd\u00fe\u0007\u0007\u0000\u0000\u00feL\u0001"+
		"\u0000\u0000\u0000\u00ff\u0100\u0005f\u0000\u0000\u0100\u0101\u0007\u0002"+
		"\u0000\u0000\u0101\u0102\u0007\u0001\u0000\u0000\u0102\u0103\u0007\u0003"+
		"\u0000\u0000\u0103\u0104\u0007\u0007\u0000\u0000\u0104N\u0001\u0000\u0000"+
		"\u0000\u0105\u0109\u0007\u0011\u0000\u0000\u0106\u0108\u0003S(\u0000\u0107"+
		"\u0106\u0001\u0000\u0000\u0000\u0108\u010b\u0001\u0000\u0000\u0000\u0109"+
		"\u0107\u0001\u0000\u0000\u0000\u0109\u010a\u0001\u0000\u0000\u0000\u010a"+
		"P\u0001\u0000\u0000\u0000\u010b\u0109\u0001\u0000\u0000\u0000\u010c\u0110"+
		"\u0007\u0012\u0000\u0000\u010d\u010f\u0003S(\u0000\u010e\u010d\u0001\u0000"+
		"\u0000\u0000\u010f\u0112\u0001\u0000\u0000\u0000\u0110\u010e\u0001\u0000"+
		"\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111R\u0001\u0000\u0000"+
		"\u0000\u0112\u0110\u0001\u0000\u0000\u0000\u0113\u0114\u0007\u0013\u0000"+
		"\u0000\u0114T\u0001\u0000\u0000\u0000\u0115\u0117\u0007\u0014\u0000\u0000"+
		"\u0116\u0115\u0001\u0000\u0000\u0000\u0117\u0118\u0001\u0000\u0000\u0000"+
		"\u0118\u0116\u0001\u0000\u0000\u0000\u0118\u0119\u0001\u0000\u0000\u0000"+
		"\u0119\u011a\u0001\u0000\u0000\u0000\u011a\u011b\u0006)\u0000\u0000\u011b"+
		"V\u0001\u0000\u0000\u0000\u011c\u011e\u0003Y+\u0000\u011d\u011c\u0001"+
		"\u0000\u0000\u0000\u011e\u011f\u0001\u0000\u0000\u0000\u011f\u011d\u0001"+
		"\u0000\u0000\u0000\u011f\u0120\u0001\u0000\u0000\u0000\u0120X\u0001\u0000"+
		"\u0000\u0000\u0121\u0122\u0007\u0015\u0000\u0000\u0122Z\u0001\u0000\u0000"+
		"\u0000\u0123\u0124\u0005\"\u0000\u0000\u0124\u0125\u0006,\u0001\u0000"+
		"\u0125\u0126\u0001\u0000\u0000\u0000\u0126\u0127\u0006,\u0002\u0000\u0127"+
		"\u0128\u0006,\u0003\u0000\u0128\\\u0001\u0000\u0000\u0000\u0129\u012a"+
		"\b\u0016\u0000\u0000\u012a\u012b\u0006-\u0004\u0000\u012b\u012c\u0001"+
		"\u0000\u0000\u0000\u012c\u012d\u0006-\u0003\u0000\u012d^\u0001\u0000\u0000"+
		"\u0000\u012e\u012f\u0005\\\u0000\u0000\u012f\u0130\u0007\u0017\u0000\u0000"+
		"\u0130\u0131\u0006.\u0005\u0000\u0131\u0132\u0001\u0000\u0000\u0000\u0132"+
		"\u0133\u0006.\u0003\u0000\u0133`\u0001\u0000\u0000\u0000\u0134\u0135\u0005"+
		"\\\u0000\u0000\u0135\u0136\b\u0017\u0000\u0000\u0136\u0137\u0006/\u0006"+
		"\u0000\u0137\u0138\u0001\u0000\u0000\u0000\u0138\u0139\u0006/\u0003\u0000"+
		"\u0139b\u0001\u0000\u0000\u0000\u013a\u013b\u0005\n\u0000\u0000\u013b"+
		"\u013c\u00060\u0007\u0000\u013c\u013d\u0001\u0000\u0000\u0000\u013d\u013e"+
		"\u00060\b\u0000\u013e\u013f\u00060\t\u0000\u013fd\u0001\u0000\u0000\u0000"+
		"\u0140\u0144\u0005\u0000\u0000\u0000\u0141\u0143\t\u0000\u0000\u0000\u0142"+
		"\u0141\u0001\u0000\u0000\u0000\u0143\u0146\u0001\u0000\u0000\u0000\u0144"+
		"\u0145\u0001\u0000\u0000\u0000\u0144\u0142\u0001\u0000\u0000\u0000\u0145"+
		"\u0147\u0001\u0000\u0000\u0000\u0146\u0144\u0001\u0000\u0000\u0000\u0147"+
		"\u0148\u0007\u0018\u0000\u0000\u0148\u0149\u00061\n\u0000\u0149\u014a"+
		"\u0001\u0000\u0000\u0000\u014a\u014b\u00061\b\u0000\u014b\u014c\u0006"+
		"1\t\u0000\u014cf\u0001\u0000\u0000\u0000\u014d\u014e\u0005\\\u0000\u0000"+
		"\u014e\u014f\u0005\u0000\u0000\u0000\u014f\u0153\u0001\u0000\u0000\u0000"+
		"\u0150\u0152\t\u0000\u0000\u0000\u0151\u0150\u0001\u0000\u0000\u0000\u0152"+
		"\u0155\u0001\u0000\u0000\u0000\u0153\u0154\u0001\u0000\u0000\u0000\u0153"+
		"\u0151\u0001\u0000\u0000\u0000\u0154\u0156\u0001\u0000\u0000\u0000\u0155"+
		"\u0153\u0001\u0000\u0000\u0000\u0156\u0157\u0007\u0018\u0000\u0000\u0157"+
		"\u0158\u00062\u000b\u0000\u0158\u0159\u0001\u0000\u0000\u0000\u0159\u015a"+
		"\u00062\b\u0000\u015a\u015b\u00062\t\u0000\u015bh\u0001\u0000\u0000\u0000"+
		"\u015c\u015d\u0005\u0000\u0000\u0001\u015d\u015e\u00063\f\u0000\u015e"+
		"\u015f\u0001\u0000\u0000\u0000\u015f\u0160\u00063\b\u0000\u0160\u0161"+
		"\u00063\t\u0000\u0161j\u0001\u0000\u0000\u0000\u0162\u0163\u0005\"\u0000"+
		"\u0000\u0163\u0164\u00064\r\u0000\u0164\u0165\u0001\u0000\u0000\u0000"+
		"\u0165\u0166\u00064\t\u0000\u0166l\u0001\u0000\u0000\u0000\u0167\u0168"+
		"\u0005-\u0000\u0000\u0168\u0169\u0005-\u0000\u0000\u0169\u016d\u0001\u0000"+
		"\u0000\u0000\u016a\u016c\b\u0019\u0000\u0000\u016b\u016a\u0001\u0000\u0000"+
		"\u0000\u016c\u016f\u0001\u0000\u0000\u0000\u016d\u016b\u0001\u0000\u0000"+
		"\u0000\u016d\u016e\u0001\u0000\u0000\u0000\u016e\u0170\u0001\u0000\u0000"+
		"\u0000\u016f\u016d\u0001\u0000\u0000\u0000\u0170\u0171\u00065\u0000\u0000"+
		"\u0171n\u0001\u0000\u0000\u0000\u0172\u0173\u0005(\u0000\u0000\u0173\u0174"+
		"\u0005*\u0000\u0000\u0174\u0175\u0001\u0000\u0000\u0000\u0175\u0176\u0006"+
		"6\u000e\u0000\u0176\u0177\u0001\u0000\u0000\u0000\u0177\u0178\u00066\u000f"+
		"\u0000\u0178\u0179\u00066\u0000\u0000\u0179p\u0001\u0000\u0000\u0000\u017a"+
		"\u017b\t\u0000\u0000\u0000\u017b\u017c\u0005\u0000\u0000\u0001\u017c\u017d"+
		"\u00067\u0010\u0000\u017d\u017e\u0001\u0000\u0000\u0000\u017e\u017f\u0006"+
		"7\b\u0000\u017fr\u0001\u0000\u0000\u0000\u0180\u0181\u0005*\u0000\u0000"+
		"\u0181\u0182\u0005)\u0000\u0000\u0182\u0183\u0001\u0000\u0000\u0000\u0183"+
		"\u0184\u00068\u0011\u0000\u0184\u0185\u0001\u0000\u0000\u0000\u0185\u0186"+
		"\u00068\t\u0000\u0186\u0187\u00068\u0000\u0000\u0187t\u0001\u0000\u0000"+
		"\u0000\u0188\u0189\t\u0000\u0000\u0000\u0189\u018a\u0001\u0000\u0000\u0000"+
		"\u018a\u018b\u00069\u0000\u0000\u018bv\u0001\u0000\u0000\u0000\u018c\u018d"+
		"\u0005(\u0000\u0000\u018d\u018e\u0005*\u0000\u0000\u018e\u018f\u0001\u0000"+
		"\u0000\u0000\u018f\u0190\u0006:\u0012\u0000\u0190\u0191\u0001\u0000\u0000"+
		"\u0000\u0191\u0192\u0006:\u0000\u0000\u0192\u0193\u0006:\u000f\u0000\u0193"+
		"x\u0001\u0000\u0000\u0000\u0194\u0195\u0005*\u0000\u0000\u0195\u0196\u0005"+
		")\u0000\u0000\u0196\u0197\u0001\u0000\u0000\u0000\u0197\u0198\u0005\u0000"+
		"\u0000\u0001\u0198\u0199\u0006;\u0013\u0000\u0199z\u0001\u0000\u0000\u0000"+
		"\u019a\u019b\u0005*\u0000\u0000\u019b\u019c\u0005)\u0000\u0000\u019c\u019d"+
		"\u0001\u0000\u0000\u0000\u019d\u019e\u0006<\u0014\u0000\u019e\u019f\u0001"+
		"\u0000\u0000\u0000\u019f\u01a0\u0006<\b\u0000\u01a0|\u0001\u0000\u0000"+
		"\u0000\u01a1\u01a2\t\u0000\u0000\u0000\u01a2~\u0001\u0000\u0000\u0000"+
		"\n\u0000\u0001\u0002\u0109\u0110\u0118\u011f\u0144\u0153\u016d\u0015\u0006"+
		"\u0000\u0000\u0001,\u0000\u0005\u0001\u0000\u0003\u0000\u0000\u0001-\u0001"+
		"\u0001.\u0002\u0001/\u0003\u00010\u0004\u00072\u0000\u0004\u0000\u0000"+
		"\u00011\u0005\u00012\u0006\u00013\u0007\u00014\b\u00016\t\u0005\u0002"+
		"\u0000\u00017\n\u00018\u000b\u0001:\f\u0001;\r\u0001<\u000e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}