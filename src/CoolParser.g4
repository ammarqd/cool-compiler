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
    : OBJECTID PARENT_OPEN (formal (COMMA formal)*)? PARENT_CLOSE COLON TYPEID CURLY_OPEN expr CURLY_CLOSE // Method declaration
    | OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)? // Variable declaration and assignment
    ;

formal : OBJECTID COLON TYPEID;

expr
    : assignmentExpr
    ;

assignmentExpr
    : negationExpr
    | OBJECTID ASSIGN_OPERATOR expr
    | LET OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)? (COMMA OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?)* IN expr
    ;

negationExpr
    : comparisonExpr
    | NOT expr
    ;

comparisonExpr
    : defaultExpr
    | defaultExpr (LESS_EQ_OPERATOR | LESS_OPERATOR | EQ_OPERATOR) defaultExpr
    ;

defaultExpr
    : defaultExpr PERIOD OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE
    | defaultExpr AT TYPEID PERIOD OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE
    | OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE
    | INT_COMPLEMENT_OPERATOR expr
    | ISVOID expr
    | PARENT_OPEN expr PARENT_CLOSE
    | defaultExpr (MULT_OPERATOR | DIV_OPERATOR) defaultExpr
    | defaultExpr (PLUS_OPERATOR | MINUS_OPERATOR) defaultExpr
    | NOT expr
    | LET OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)? (COMMA OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?)* IN expr
    | OBJECTID ASSIGN_OPERATOR expr
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