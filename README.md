# CompileAPT

Compile Java sources during Annotation Processing to analyze class structures.

# Why Compile Java Sources during Annotation Processing?

The main porpuse of CompileAPT is: write a processor that can process Annotation at Runtime and during Annotation Processing, and easy to maintain.

# How CompileAPT compile Java Sources?

CompileAPT will get the Source file during annotation Processing (using Trees API) and delegate source compile to Javac. (By default, your tools.jar is packed into CompileAPT jar generated by Gradle).

# How to use CompileAPT?

You need to implements the processor: 'com.github.jonathanxd.compiledapt.service.CompiledProcessor'. And provide the service (like what you do for the Annotation Processor Services)

# Processing, Analyzing & Generating classes

**TODO**
