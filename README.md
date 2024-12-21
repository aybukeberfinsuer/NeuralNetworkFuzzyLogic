<h1>Sakarya University Fuzzy Logic - Assignment 2</h1>

<p>This repository contains the implementation of the 2nd assignment for the Sakarya University Fuzzy Logic course. The project demonstrates the application of neural networks for solving a problem using Neuroph and JFreeChart libraries, alongside a custom dataset.</p>

<h2>Key Features</h2>
<ul>
  <li><strong>Custom Dataset</strong>: The <code>salary_data.csv</code> file is prepared using data from the FuzzyLogic repository and loaded into the project via the <code>DataLoader</code> class.</li>
  <li><strong>Topology Experimentation</strong>: Different neural network topologies were tested, and the best-performing topology was selected for further experiments.</li>
  <li><strong>Momentum and Non-Momentum Training</strong>: The neural network was trained and tested with both momentum and non-momentum approaches to compare their performance.</li>
  <li><strong>Execution</strong>: The compiled program can be run by navigating to the <code>dist</code> folder and executing the command <code>java -jar program.jar</code>.</li>
  <li><strong>Libraries Used</strong>: 
    <ul>
      <li><strong>Neuroph</strong>: For creating and training neural networks.</li>
      <li><strong>JFreeChart</strong>: For visualizing data and results.</li>
    </ul>
  </li>
  <li><strong>Documentation</strong>: A detailed report about the project and its results is available in the <code>doc</code> directory.</li>
</ul>

<h2>How to Use</h2>
<ol>
  <li><strong>Run the Program</strong>:  
    <ul>
      <li>Navigate to the <code>dist</code> folder.</li>
      <li>Execute the command: <code>java -jar program.jar</code>.</li>
    </ul>
  </li>
  <li><strong>Dependencies</strong>: All required <code>.jar</code> files are available in the <code>lib</code> folder. Ensure they are included in your classpath if needed.</li>
</ol>

<h2>Project Structure</h2>
<ul>
  <li><strong>DataLoader Class</strong>: Handles dataset loading and preprocessing.</li>
  <li><strong>lib</strong>: Contains all the necessary jar files for running the program.</li>
  <li><strong>dist</strong>: Includes the compiled <code>.jar</code> file for execution.</li>
  <li><strong>doc</strong>: Provides a detailed report on the project.</li>
</ul>

<p>Feel free to explore the code and documentation for further insights!</p>
