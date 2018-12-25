

class ConcreteClass():
	"""
	Implement the primitive operations to carry out
	subclass-specificsteps of the algorithm.

	def _primitive_operation_1(self):
		pass

	def _primitive_operation_2(self):
		pass
	"""


	def run(fileWrite, fileRead):



		with open(fileWrite,'w+') as fileW:
			with open(fileRead) as fileR:
				lines = fileR.readlines()

				for line in lines:
					line2 = line[0:-9] + ';\n'
					fileW.write(line2)


def main():
	fileWrite = 'Ya.txt'
	fileRead = 'timeline99.txt'

	ConcreteClass.run(fileWrite, fileRead)



if __name__ == "__main__":
	main()


