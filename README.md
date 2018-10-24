# parser
## DOWNLOAD
[latest version](https://github.com/Literous/parser/releases/tag/v1.0)
## FEATRUE
- create FIRST & FOLLOW
- create LL analysis form
- create SLR analysis form and items
- create LR1 analysis form and items
## HOW TO USE
```
java -jar <runable JAR path> <your language path>
```
## ENVIROMENT
Above jdk1.8.0
## LANGUAGE FORMAT
The input language must be augmented language(added S1 -> S)
- augmented production must be in the first line
- one production per line
- format of production:
    ```
    <E> ::= <E> "+" <T>
    ```
- the non-terminal should be surrounded by angle brackets
- the token(terminal) should be surrounded by quotation
## OUTPUT
- the result will be displayed in command line
- It will create result file in the parent directory of your language file
### note
It will still create LL analysis form, even if your language is not LL as result of lacking examination of LL conflict<br>
However, if your language is not SLR or LR1, Accordingly, it will not create corresponding analysis form or items
### EXAMPLE
#### input language
```
<E1> ::= <E>
<E> ::= <E> "+" <T>
<E> ::= <T>
<T> ::= <T> "*" <F>
<T> ::= <F>
<F> ::= "(" <E> ")"
<F> ::= "id"
```
#### output
```
FIRST
T:	{(, id}
E:	{(, id}
F:	{(, id}

FOLLOW
T:	{$, ), *, +}
E:	{$, ), +}
F:	{$, ), *, +}
E1:	{$}
```
state|(|)|*|+|id|$|T|E|F|
-|-|-|-|-|-|-|-|-|-|
0|s4||||s5||1|2|3|
1||r2|s6|r2||r2||||
2||||s7||r0||||
3||r4|r4|r4||r4||||
4|s4||||s5||1|8|3|
5||r6|r6|r6||r6||||
6|s4||||s5||||10|
7|s4||||s5||11||3|
8||s9||s7||||||
9||r5|r5|r5||r5||||
10||r3|r3|r3||r3||||
11||r1|s6|r1||r1||||
