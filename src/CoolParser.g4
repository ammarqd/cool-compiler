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
    : IF expr THEN expr ELSE expr FI                                                        # Conditional
    | WHILE expr LOOP expr POOL                                                             # Loop
    | CURLY_OPEN (expr SEMICOLON)+ CURLY_CLOSE                                              # Block
    | CASE expr OF (OBJECTID COLON TYPEID RIGHTARROW expr SEMICOLON)+ ESAC                  # Case
    | LET OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?
                (COMMA OBJECTID COLON TYPEID (ASSIGN_OPERATOR expr)?)* IN expr              # Let
    | OBJECTID ASSIGN_OPERATOR expr                                                         # Assign
    | NOT expr                                                                              # Not
    | comparisonExpr                                                                        # Comparison
    ;


comparisonExpr
    : primaryExpr (LESS_EQ_OPERATOR | LESS_OPERATOR | EQ_OPERATOR) primaryExpr
    | primaryExpr
    ;

primaryExpr
    : primaryExpr AT TYPEID PERIOD OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE  # StaticDispatch
    | primaryExpr PERIOD OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE            # DynamicDispatch
    | OBJECTID PARENT_OPEN (expr (COMMA expr)*)? PARENT_CLOSE                               # MethodCall
    | INT_COMPLEMENT_OPERATOR expr                                                          # Complement
    | ISVOID expr                                                                           # IsVoid
    | PARENT_OPEN expr PARENT_CLOSE                                                         # Parenthesis
    | primaryExpr (MULT_OPERATOR | DIV_OPERATOR) expr                                       # MultDiv
    | primaryExpr (PLUS_OPERATOR | MINUS_OPERATOR) expr                                     # AddSub
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