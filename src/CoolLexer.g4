/**
 * Define lexer rules for Cool
 */
lexer grammar CoolLexer;
@members {
    int commentLevel = 0;
}

/* Punctuation */
PERIOD              : '.';
COMMA               : ',';
AT                  : '@';
SEMICOLON           : ';';
COLON               : ':';
CURLY_OPEN          : '{' ;
CURLY_CLOSE         : '}' ;
PARENT_OPEN         : '(' ;
PARENT_CLOSE        : ')' ;

/* Operators */
PLUS_OPERATOR       : '+';
MINUS_OPERATOR      : '-';
MULT_OPERATOR       : '*';
DIV_OPERATOR        : '/';
INT_COMPLEMENT_OPERATOR     : '~';
LESS_OPERATOR               : '<';
GREAT_OPERATOR              : '>';
LESS_EQ_OPERATOR            : '<=';
EQ_OPERATOR                 : '=' ;
ASSIGN_OPERATOR 	        : '<-';
RIGHTARROW                  : '=>';
NOT                         : 'not';

/* Keywords */
CLASS : [cC][lL][aA][sS][sS];
INHERITS : [iI][nN][hH][eE][rR][iI][tT][sS];
MAIN : [mM][aA][iI][nN];
IF : [iI][fF];
THEN : [tT][hH][eE][nN];
ELSE : [eE][lL][sS][eE];
FI : [fF][iI];
WHILE : [wW][hH][iI][lL][eE];
LOOP : [lL][oO][oO][pP];
POOL : [pP][oO][oO][lL];
LET : [lL][eE][tT];
IN : [iI][nN];
CASE : [cC][aA][sS][eE];
OF : [oO][fF];
ESAC : [eE][sS][aA][cC];
NEW : [nN][eE][wW];
ISVOID : [iI][sS][vV][oO][iI][dD];
TRUE : 't'[rR][uU][eE];
FALSE : 'f'[aA][lL][sS][eE];

/* Identifiers and Types */
TYPEID : [A-Z]CHAR*;
OBJECTID : [a-z]CHAR*;
fragment CHAR : [a-zA-Z0-9_];

/* Int Literals */
INT_CONST : DIGIT+;
fragment DIGIT : [0-9];

/* String Literals */
BEGIN_STRING : '"' -> pushMode(STRING_MODE), more;
mode STRING_MODE;
STR_TEXT : (~[\r\n"\u0000\\]) -> more;
STR_ESC : ('\\' [bftnr"\\\r\n]) -> more;

UNTERMINATED_STRING : '\n'
{ setText("Unterminated string constant"); }
-> type(ERROR), popMode;

NULL_STRING : '\u0000' .*? EOF
{ setText("String contains null character."); }
-> type(ERROR), popMode;

ESC_NULL : '\\\u0000' .*? EOF
{ setText("String contains escaped null character."); }
-> type(ERROR), popMode;

EOF_STRING : EOF
{ setText("EOF in string constant"); }
-> type(ERROR), popMode;

STR_CONST : '"' -> popMode;

mode DEFAULT_MODE;

/* Whitespace and Comments */

LINE_COMMENT  : '--' ~[\r\n]* -> skip;
BEGIN_COMMENT: '(*' { commentLevel++; } -> pushMode(COMMENT_MODE), skip;

mode COMMENT_MODE;

EOF_COMMENT : '\n' EOF
{ setText("EOF in comment"); }
-> type(ERROR);

END_COMMENT:
    '*)' {
        commentLevel--;
    } -> popMode, skip;

COMMENT_TEXT: . -> skip;  // Skip any character that's not part of '(*' or '*)'

BEGIN_INNER_COMMENT:
    '(*' {
        commentLevel++;
    } -> skip, pushMode(COMMENT_MODE);

ERROR_UNCLOSED_COMMENT:
    '*)' EOF {
        commentLevel--;
        if (commentLevel > 0) {
            setText("EOF in comment");
            setType(ERROR);
        } else {
            skip();
        }
    };

mode DEFAULT_MODE;

UNMATCHED_PAREN : '*)'
{ setText("Unmatched *)"); }
-> type(ERROR);

WHITESPACE : (' ' | '\n' | '\f' | '\r' | '\t' | '\u000B')+ -> skip;

/* Catch-all for unexpected characters */
ERROR : . ;
