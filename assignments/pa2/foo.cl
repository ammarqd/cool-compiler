class Main {
    x : Int <- 5;
    blah : String <- "foo";

    main(): Object {
    {
        5@Int.ghjfg();
                foo(10);
        gjhg();
        }
    };

    foo(a: Int): Int {
        let b : Int <- a in {
            x <- b;
            b;
        }
    };

    foo() : Object {
        5
    };

    blah() : String {
        let hello : String <- "hi" in {
            "hola";
        }
    };
};

class A {
    y : String;

    bar(s: String): String {
        let a : String <- "blah" in
            let b : String <- "blah2" in
                let c : String <- "blah3" in
                    let d : String <- "blah4", e : String <- "hello" in
                        let x : String <- a.concat(b).concat(c).concat(d) in
                            "a"

    };
};

class B {

    bar() : String {
        self <- "a"
    };

    hello() : Int {
        3
    };

};