x = "global"
print('iniciamos')

def foo():
    print("x inside :", x)
    x = 'modificado'
    print("x inside :", x)


foo()
print("x outside:", x)

if __name__ == '__main__':
	foo()