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

feature :
    OBJECTID PARENT_OPEN formalList? PARENT_CLOSE COLON TYPEID CURLY_OPEN expr CURLY_CLOSE
    | OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?;

formalList : formal (COMMA formal)*;

formal : OBJECTID SEMICOLON TYPEID;

expr : ;
