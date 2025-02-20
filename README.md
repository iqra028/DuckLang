#  DuckLang Keywords  

DuckLang has a set of predefined keywords that define data types, operations, and control structures. Below is a list of all keywords with their meanings.

---

##  Data Types  
| Keyword             | Description                          | Regex Pattern            |  
|---------------------|----------------------------------|--------------------------|  
| `FEATHER_CODE`     | Represents a single character (a-z, A-Z, 0-9). | `[a-zA-Z0-9]`  |  
| `DUCK_ID`         | Variable name (lowercase letters only). | `[a-z]+`  |  
| `DUCK_BOOL`       | Boolean values: `QUACK_QUACK` (true) or `Quaak` (false). | `QUACK_QUACK|Quaak`  |  
| `WEBBED_FEET`     | Integer values (including negative numbers). | `-?[0-9]+`  |  
| `DUCK_POND_DEPTH` | Floating-point numbers. | `[0-9]+[\.0-9]*`  |  

---

##  Operators  
| Keyword         | Description       | Regex Pattern         |  
|----------------|------------------|----------------------|  
| `ADD`         | Addition          | `ADD`  |  
| `SUB`         | Subtraction       | `SUB`  |  
| `MUL`         | Multiplication    | `MUL`  |  
| `DIV`         | Division          | `DIV`  |  
| `MOD`         | Modulus           | `MOD`  |  
| `POW`         | Exponentiation    | `POW`  |  

---

##  Variable Scope  
| Keyword         | Description                         | Regex Pattern   |  
|----------------|---------------------------------|-----------------|  
| `Nest_Egg`     | Declares a global variable.     | `Nest_Egg`      |  
| `DUCK_LOCAL`   | Represents local variables.     | `[a-zA-Z0-9]+`  |  

---

##  Comments  
| Keyword                 | Description                          | Regex Pattern      |  
|-------------------------|----------------------------------|-------------------|  
| `DUCK_COMMENT_SINGLE`  | Single-line comment (`~QUACK ...`). | `~QUACK.*`  |  
| `DUCK_COMMENT_MULTI`   | Multi-line comment (`{ ... }`). | `{.*}`  |  

---

##  Other Keywords  
| Keyword          | Description               |  
|-----------------|--------------------------|  
| `QUACK_PRINT`   | Prints output.           |  
| `QUACK_INPUT`   | Takes input from the user. |  

---
These keywords define DuckLang's fundamental building blocks, ensuring structured syntax and execution.
