# cool-compiler
A complete compiler for the Cool programming language, coded in Java and using ANTLR4 for parse tree generation.

- [x] Lexer (63/63 test cases passed)
- [x] Parser (71/71 test cases passed)
- [x] Semantic Analysis (74/74 test cases passed)
- [x] Code Generation (67/67 test cases passed)

<br>

To test the pass/fail cases, in the relevant folder (pa1, pa2, pa3) :

```assignments/pa1:``` 
```
buildme frontend
testme lexer parser
```

and to compare the frontend grammar output with the reference:

```
./myfrontend foo.cl
./reffrontend foo.cl
```

<br>

```assignments/pa2:``` 
```
buildme semant
testme semant
```

and to compare the semantic analysis output with the reference

```
./mybackend foo.cl
./refbackend foo.cl
```

<br>

```assignments/pa3:``` 
```
buildme backend
testme backend
```

and to compare the generated MIPS assembly with the reference:

```
./mybackend foo.cl
./refbackend foo.cl
```

then run the command ```coolspim -file foo.s``` on the generated `foo.s` file
