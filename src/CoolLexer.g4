/**
 * Define a lexer rules for Cool
 */
lexer grammar CoolLexer;

/* Punctution */

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

TRUE : [tT][rR][uU][eE];
FALSE : [fF][aA][lL][sS][eE];

TYPE : [A-Z][a-zA-Z0-9_]*;
ID : [a-zA-Z_][a-zA-Z0-9_]*;

/* Literals */

INTEGER : [0-9]+;
STRING : '"' (ESC | ~["\\])* '"';

/* Whitespace and comments */
WHITESPACE : (' ' | '\n' | '\r' | '\t' | '\u000B')+ -> skip;
COMMENT_BLOCK : '(*' .*? '*)' -> skip;
COMMENT_LINE : '--' -> skip;
ESC : '\\';

/* Catch-all for unexpected characters */

ERROR : . ;
