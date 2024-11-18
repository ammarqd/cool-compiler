class Main {
    x : Int <- 5;
    blah : String <- "foo";

    foo(a: Int): Int {
        let b : Int <- a + 1 in {
            x <- b;
            b;
        }
    };

    main(): Object {
        foo(10)
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
                            x
    };
};

class B {

    bar() : String {
        "a"
    };

    hello() : Int {
        3
    };

};