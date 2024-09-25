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
    : OBJECTID PARENT_OPEN formalList? PARENT_CLOSE COLON TYPEID CURLY_OPEN expr CURLY_CLOSE // Method declaration
    | OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)? // Variable declaration and assignment
    ;

formalList : formal (COMMA formal)*;

formal : OBJECTID COLON TYPEID;

exprList : expr (COMMA expr)*;

expr
    : expr PERIOD OBJECTID PARENT_OPEN exprList? PARENT_CLOSE // Static Dispatch
    | expr AT TYPEID PERIOD OBJECTID PARENT_OPEN exprList? PARENT_CLOSE // Dynamic Dispatch
    | INT_COMPLEMENT_OPERATOR expr
    | ISVOID expr
    | PARENT_OPEN expr PARENT_CLOSE
    | expr (MULT_OPERATOR | DIV_OPERATOR) expr
    | expr (PLUS_OPERATOR | MINUS_OPERATOR) expr
    | expr (LESS_EQ_OPERATOR | LESS_OPERATOR | EQ_OPERATOR) expr
    | NOT expr
    | OBJECTID ASSIGN_OPERATOR expr
    | LET OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)? (COMMA OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?)* IN expr
    | OBJECTID PARENT_OPEN exprList? PARENT_CLOSE
    | IF expr THEN expr ELSE expr FI
    | WHILE expr LOOP expr POOL
    | CURLY_OPEN (expr SEMICOLON)+ CURLY_CLOSE
    | CASE expr OF (OBJECTID COLON TYPEID RIGHTARROW expr SEMICOLON)+ ESAC
    | NEW TYPEID
    | OBJECTID
    | INT_CONST
    | STR_CONST
    | TRUE
    | FALSE
    ;

error : ERROR
{ Utilities.lexError(); }
;