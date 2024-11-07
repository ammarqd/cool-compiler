# Cool-Compiler
A Compiler for the Cool programming language, written in Java and utilising ANTLR to define the grammar.

- [x] Lexer (63/63 test cases passed!)
- [x] Parser (71/71 test cases passed!)
- [ ] Semantic Analysis (WIP - Semant branch)
- [ ] Code Generation

<br>

To build and test the frontend pass/fail cases, within the ```assignments/pa1``` folder: 

```
buildme frontend
testme lexer parser
```
To test just the lexer, you can simply run ```testme lexer```

<br>

To compare the frontend grammar output with the reference, within the ```assignments/pa1``` folder: 
```
buildme frontend
./myfrontend good.cl
./reffrontend good.cl
```
- ```good.cl``` can be replaced with any Cool programming file to test various outputs  
- the ```-x``` tag can be added to the test commands like so, ```./myfrontend -x good.cl``` or ```./reffrontend -x good.cl``` to simply test the lexer grammar (default is to print the abstract syntax tree from the parser). 

