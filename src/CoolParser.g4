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
    : OBJECTID PARENT_OPEN (formal (COMMA formal)*)? PARENT_CLOSE COLON TYPEID CURLY_OPEN expr CURLY_CLOSE # Method
    | OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)? # Attribute
    ;

formal : OBJECTID COLON TYPEID;

expr
    : defaultExpr
    | comparisonExpr
    ;

comparisonExpr
    : defaultExpr (LESS_EQ_OPERATOR | LESS_OPERATOR | EQ_OPERATOR) defaultExpr
    ;

defaultExpr
    : defaultExpr AT TYPEID PERIOD OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE  # StaticDispatch
    | defaultExpr PERIOD OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE            # DynamicDispatch
    | OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE                               # MethodCall
    | INT_COMPLEMENT_OPERATOR defaultExpr                                                   # Complement
    | ISVOID defaultExpr                                                                    # IsVoid
    | PARENT_OPEN expr PARENT_CLOSE                                                         # Parenthesis
    | defaultExpr (MULT_OPERATOR | DIV_OPERATOR) defaultExpr                                # MultDiv
    | defaultExpr (PLUS_OPERATOR | MINUS_OPERATOR) defaultExpr                              # AddSub
    | NOT expr                                                                              # Not
    | OBJECTID ASSIGN_OPERATOR expr                                                         # Assign
    | LET OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?
      (COMMA OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?)* IN expr                        # Let
    | IF expr THEN expr ELSE expr FI                                                        # Conditional
    | WHILE expr LOOP expr POOL                                                             # Loop
    | CURLY_OPEN (expr SEMICOLON)+ CURLY_CLOSE                                              # Block
    | CASE expr OF (OBJECTID COLON TYPEID RIGHTARROW expr SEMICOLON)+ ESAC                  # Case
    | NEW TYPEID                                                                            # New
    | OBJECTID                                                                              # Object
    | INT_CONST                                                                             # Integer
    | STR_CONST                                                                             # String
    | TRUE                                                                                  # True
    | FALSE                                                                                 # False
    ;

error : ERROR
{ Utilities.lexError(); }
;