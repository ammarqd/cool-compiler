/**
 * Define a grammar for Cool
 */
parser grammar CoolParser;

options { tokenVocab = CoolLexer; }


/*  Starting point for parsing a Cool file  */

program 
	: (coolClass SEMICOLON)+ EOF
	;

coolClass :
	CLASS TYPEID (INHERITS TYPEID)? CURLY_OPEN (feature SEMICOLON)* CURLY_CLOSE;

feature
    : OBJECTID PARENT_OPEN formalList? PARENT_CLOSE COLON TYPEID CURLY_OPEN expr CURLY_CLOSE
    | OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?
    ;

formalList : formal (COMMA formal)*;

formal : OBJECTID COLON TYPEID;

exprList : expr (COMMA expr)*;

expr
    : OBJECTID ASSIGN_OPERATOR expr
    | expr (AT TYPEID)? PERIOD OBJECTID exprList?
    | OBJECTID exprList?
    | IF expr THEN expr ELSE expr FI
    | WHILE expr LOOP expr POOL
    | CURLY_OPEN (expr SEMICOLON)+ CURLY_CLOSE
    | LET OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)? (COMMA OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?)* IN expr
    | CASE expr OF (OBJECTID COLON TYPEID RIGHTARROW expr SEMICOLON)+ ESAC
    | NEW TYPEID
    | ISVOID expr
    | expr PLUS_OPERATOR expr
    | expr MINUS_OPERATOR expr
    | expr MULT_OPERATOR expr
    | expr DIV_OPERATOR expr
    | INT_COMPLEMENT_OPERATOR expr
    | expr LESS_OPERATOR expr
    | expr LESS_EQ_OPERATOR expr
    | expr EQ_OPERATOR expr
    | NOT expr
    | PARENT_OPEN expr PARENT_CLOSE
    | OBJECTID
    | INT_CONST
    | STR_CONST
    | TRUE
    | FALSE
    ;
