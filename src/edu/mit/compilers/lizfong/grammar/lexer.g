header {package edu.mit.compilers.lizfong.grammar;}

options 
{
  mangleLiteralPrefix = "TK_";
  language="Java";
}

class DecafScanner extends Lexer;
options 
{
  k = 2;
}

tokens 
{
  "boolean";
  "break";
  "callout";
  "class";
  "continue";
  "else";
  "false";
  "for";
  "if";
  "int";
  "return";
  "true";
  "void";
}

// Symbols sadly cannot be listed as generic tokens.
// Individually list each one along with a sane name.
LCURLY options { paraphrase = "{"; } : "{";
RCURLY options { paraphrase = "}"; } : "}";
LSQUARE options { paraphrase = "["; } : "[";
RSQUARE options { paraphrase = "]"; } : "]";
LPAREN options { paraphrase = "("; } : "(";
RPAREN options { paraphrase = ")"; } : ")";
EQUALS options { paraphrase = "=="; } : "==";
NOT_EQUALS options { paraphrase = "!="; } : "!=";
LOGICAL_AND options { paraphrase = "&&"; } : "&&";
LOGICAL_OR options { paraphrase = "||"; } : "||";
PLUS options { paraphrase = "+"; } : "+";
MINUS options { paraphrase = "-"; } : "-";
TIMES options { paraphrase = "*"; } : "*";
DIVIDE options { paraphrase = "/"; } : "/";
MODULO options { paraphrase = "%"; } : "%";
NOT options { paraphrase = "!"; } : "!";
LT options { paraphrase = "<"; } : "<";
GT options { paraphrase = ">"; } : ">";
LE options { paraphrase = "<="; } : "<=";
GE options { paraphrase = ">="; } : ">=";
ASSIGN options { paraphrase = "="; } : "=";
INC_ASSIGN options { paraphrase = "+="; } : "+=";
DEC_ASSIGN options { paraphrase = "-="; } : "-=";
SEMICOLON options { paraphrase = ";"; } : ";";
COMMA options { paraphrase = ","; } : ",";

// Identifier is the catch-all bucket for any alphanumeric token.
// Typically method or variable names.
ID options { paraphrase = "an identifier"; } : 
  ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;

// Elide whitespace - spaces, tabs, and newlines.
WS_ : (' ' | '\t' | '\n' {newline();}) {_ttype = Token.SKIP; };

// Elide comments by stripping out anything after a // until the next line.
SL_COMMENT : "//" (~'\n')* '\n' {_ttype = Token.SKIP; newline(); };

// For characters and strings, manually construct the whitelist of valid
// printing characters by removing the characters in ["'\].
// Allow escape sequences, however.
CHAR : '\'' (ESC|' '..'!'|'#'..'&'|'('..'['|']'..'~') '\'';
STRING : '"' (ESC|' '..'!'|'#'..'&'|'('..'['|']'..'~')* '"';

// Integers are straightforward - they are either in decimal or hex.
INT : (('0'..'9')+ | "0x" ('0'..'9' | 'a'..'f' | 'A'..'F')+);

// Escape sequences consist of \n, \", \t, \\, and \'.
// Also match anything else but return an explicit error.
// Otherwise, the ESC rule fails to match causing CHAR/STRING to not match,
// and the ID rule is used instead  to swallow the middle of the char/string.
ESC :  '\\' ('n'|'"'|'t'|'\\'|'\''|
             ~('n'|'"'|'t'|'\\'|'\'') {if (true) throw new NoViableAltForCharException((char)LA(0), getFilename(), getLine(), getColumn() - 1);});
